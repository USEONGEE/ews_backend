package dragonfly.ews.domain.file.aop.strategy;

import dragonfly.ews.domain.file.aop.postprocessor.ColumnTypeCheckPostProcessor;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileContainLogsResponseDto;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileUpdateDto;
import dragonfly.ews.domain.file.exception.ExtensionMismatchException;
import dragonfly.ews.domain.file.exception.FileNotInProjectException;
import dragonfly.ews.domain.file.exception.NoFileNameException;
import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLogToken;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogRepository;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogTokenRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static dragonfly.ews.domain.file.domain.FileExtension.*;

@RequiredArgsConstructor
@Component
public class ExcelMemberFileStrategyV2 implements MemberFileStrategy {
    private final FileUtils memberFileUtils;
    private final MemberFileRepository memberFileRepository;
    private final ExcelMemberFileLogTokenRepository excelMemberFileLogTokenRepository;
    private final ExcelMemberFileLogRepository excelMemberFileLogRepository;
    private final ColumnTypeCheckPostProcessor columnTypeCheckPostProcessor;

    @Value("${server.url}")
    private String serverUrl;

    @Value("${analysis.server.url}")
    private String analysisServerUri;
    @Value("${analysis.server.column-check-uri}")
    private String columnCheckUri;
    private final WebClient webClient;

    @Override
    public boolean canSupport(FileExtension fileExtension) {
        return fileExtension == CSV || fileExtension == XLS || fileExtension == XLSX;
    }

    @Override
    @Transactional
    public MemberFile createMemberFile(Member owner, MemberFileCreateDto memberFileCreateDto) {
        // 파일명 검증
        String originalFilename = memberFileCreateDto.getFile().getOriginalFilename();
        if (originalFilename.isEmpty()) {
            throw new NoFileNameException("사용자가 제공한 파일에 이름이 없습니다.");
        }
        String savedFilename = memberFileUtils.createSavedFilename(originalFilename);

        // MemberFile 생성
        MemberFile memberFile = new MemberFile(owner,
                memberFileCreateDto.getFileName(),
                memberFileCreateDto.getFile().getOriginalFilename());

        // MemberFileLog 생성 및 의존관계 주입
        ExcelMemberFileLog excelMemberFileLog = new ExcelMemberFileLog(memberFile, savedFilename);
        excelMemberFileLog.changeDescription(memberFileCreateDto.getDescription());
        memberFile.addMemberFileLog(excelMemberFileLog);
        memberFileRepository.save(memberFile);
        // 인증 토큰 생성
        ExcelMemberFileLogToken token = ExcelMemberFileLogToken.builder()
                .id(excelMemberFileLog.getId())
                .token(UUID.randomUUID().toString())
                .expiration(3600L)
                .build();
        excelMemberFileLogTokenRepository.save(token);


        // http body 작성
        String fullPath = memberFileUtils.getFullPath(savedFilename);
        String callbackUrl = createCallbackUrl(excelMemberFileLog.getId());
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(fullPath));
        builder.part("callbackUrl", callbackUrl);
        builder.part("redisKey", token.getRedisKey());
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();

        // 외부 서버에 column check 요청
        webClient.post()
                .uri(analysisServerUri + columnCheckUri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> columnTypeCheckPostProcessor.fail(e, memberFile.getId())) // 예외 발생시 memberfile 제거
                .subscribe();

        memberFileUtils.storeFile(memberFileCreateDto.getFile(), savedFilename);
        return memberFile;
    }


    @Override
    public void updateValidate(MemberFile memberFile, MultipartFile target) {
        hasProject(memberFile);
        // 파일 확장자가 같은지
        String fileExt = memberFileUtils.extractFileExtension(target.getOriginalFilename());
        checkFileExtension(memberFile, fileExt);
        // TODO 파일 column이 같은지 + 데이터 타입이 같은지
    }

    private void hasProject(MemberFile memberFile) {
        Project project = memberFile.getProject();
        if (project == null) {
            throw new FileNotInProjectException("파일이 프로젝트에 포함되지 않았습니다.");
        }
    }

    private void checkFileExtension(MemberFile memberFile, String originalFilename) {
        if (!memberFile.isSameExt(originalFilename)) {
            throw new ExtensionMismatchException("파일 확장자가 같아야합니다.");
        }
    }

    // TODO 총 3번의 쿼리가 나간다. 수정 필요, 2번의 컬렉션 페치 조인을 해결해야함
    @Override
    public Object getMemberFileDtoById(Long memberId, Long memberFileId) {
        MemberFile memberFile = memberFileRepository.findByIdContainLogs(memberFileId)
                .orElseThrow(NoSuchFileException::new);
        return new MemberFileContainLogsResponseDto(memberFile);
    }

    @Override
    public void updateFile(MemberFile memberFile, MemberFileUpdateDto memberFileUpdateDto) {
        // 저장될 파일명 생성
        String savedFilename = memberFileUtils.createSavedFilename(memberFile.getOriginalName());
        // MemberFileLog 생성 및 저장
        ExcelMemberFileLog excelMemberFileLog = new ExcelMemberFileLog(memberFile, savedFilename);
        excelMemberFileLog.changeDescription(memberFileUpdateDto.getDescription());
        memberFile.addMemberFileLog(excelMemberFileLog);
        // 파일 저장
        memberFileUtils.storeFile(memberFileUpdateDto.getFile(), savedFilename);
    }

    private String createCallbackUrl(Object id) {
        return String.format("%sfilelog/excel/columns-type-check/callback/%s", serverUrl, id);
    }
}
