package dragonfly.ews.domain.result.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dragonfly.ews.develop.aop.LogMethodParams;
import dragonfly.ews.domain.file.domain.ExcelFileColumn;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.repository.ExcelFileColumnRepository;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.exception.NoSuchMemberFileLogException;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.dto.AnalysisExcelFileColumnDto;
import dragonfly.ews.domain.result.dto.AnalysisRequestDto;
import dragonfly.ews.domain.result.dto.UserAnalysisRequestDto;
import dragonfly.ews.domain.result.exceptioon.CannotProcessAnalysisException;
import dragonfly.ews.domain.result.exceptioon.NotCompletedException;
import dragonfly.ews.domain.result.repository.AnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.constant.Constable;
import java.util.List;

/**
 * TODO 예외메시지 국제화
 * TODO WebClient 예외 처리 4xx, 5xx
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalysisResultService {
    private final ExcelFileColumnRepository excelFileColumnRepository;
    private final MemberFileLogRepository memberFileLogRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final WebClient webClient;
    private final AnalysisResultProcessor analysisResultProcessor;
    private final MemberFileRepository memberFileRepository;
    private final ObjectMapper objectMapper;
    private final FileUtils fileUtils;
    @Value("${file.dir}")
    private String fileDir;

    @Value("${analysis.server.url}")
    private String analysisServerUri;
    @Value("${analysis.server.analysis-uri}")
    private String analysisUri;

    /**
     * [데이터 분석 요청]
     *
     * @param memberId
     * @return
     */
    @Transactional
    @LogMethodParams
    public AnalysisResult analysis(Long memberId, UserAnalysisRequestDto userAnalysisRequestDto) {
        // 분석 파일 로그 조회
        MemberFileLog memberFileLog = memberFileLogRepository.findByIdAuth(memberId, userAnalysisRequestDto.getMemberFileLodId())
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
        AnalysisResult analysisResult = new AnalysisResult(memberFileLog, AnalysisStatus.CREATED);

        // 파일 상태 조회
        validateFileAnalysisStatus(analysisResult);

        // 요청 메타데이터 생성
        FileExtension findExtension = memberFileRepository.findExtensionByMemberFileLogId(
                userAnalysisRequestDto.getMemberFileLodId()
        ).orElseThrow(NoSuchMemberFileLogException::new);
        AnalysisRequestDto analysisRequestDto = new AnalysisRequestDto(findExtension,
                null,
                userAnalysisRequestDto.isAll());
        if (!userAnalysisRequestDto.isAll()) {
            analysisRequestDto.setColumns(fetchColumnDtos(userAnalysisRequestDto.getSelectedColumnIds()));
        }

        // 파일 내용 JSON 문자열로 파싱
        String json = null;
        try {
            json = objectMapper.writeValueAsString(analysisRequestDto);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 파일 내용 가져오기
        byte[] content = fileUtils.readFileContentByPath(fileDir + memberFileLog.getSavedName());
        ByteArrayResource byteArrayResource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return memberFileLog.getSavedName(); // 파일 이름(확장자 포함)을 반환
            }
        };

        // 파일 가져오기
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", byteArrayResource);
        builder.part("metadata", json);
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();


        // 비동기 요청 보내기
        // HTML 파일은 분석 서버에서 "문자열"로 반환함
        webClient.post()
                .uri(analysisServerUri + analysisUri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(multipartBody)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(result ->
                        analysisResultProcessor.processResult((String) result, (Long) analysisResult.getId()));

        analysisResult.changeAnalysisStatus(AnalysisStatus.PROCESSING);
        return analysisResult;
    }

    /**
     * [단건 조회]
     *
     * @param memberId
     * @param analysisResultId
     * @return
     */
    public AnalysisResult findByResultId(Long memberId, Long analysisResultId) {
        return analysisResultRepository.findByIdAuth(memberId, analysisResultId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
    }

    /**
     * [전체 조회]
     *
     * @param memberId
     * @param memberFileLogId
     * @return
     */
    public List<AnalysisResult> findByFileLogId(Long memberId, Long memberFileLogId) {
        // 해당 유저가 파일 로그에 접근할 권한이 있는지 인증
        memberFileLogRepository.findByIdAuth(memberId, memberFileLogId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));

        return analysisResultRepository.findResultFileByFileLogId(memberFileLogId);
    }

    /**
     * [분석 완료된 파일 조회]
     *
     * @param memberId
     * @param analysisResultId
     * @return
     */
    public AnalysisResult findCompletedFileById(Long memberId, Long analysisResultId) {
        AnalysisResult analysisResult = analysisResultRepository.findByIdAuth(memberId, analysisResultId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
        if (analysisResult.getAnalysisStatus() != AnalysisStatus.COMPLETED) {
            throw new NotCompletedException("분석이 완료된 파일이 아닙니다.");
        }
        return analysisResult;
    }


    private List<AnalysisExcelFileColumnDto> fetchColumnDtos(List<Long> excelFileColumnIds) {
        List<ExcelFileColumn> excelFileColumns = excelFileColumnRepository.findByIdIn(excelFileColumnIds);
        return excelFileColumns.stream()
                .map(AnalysisExcelFileColumnDto::of)
                .toList();
    }

    private void validateFileAnalysisStatus(AnalysisResult analysisResult) {
        switch (analysisResult.getAnalysisStatus()) {
            case CANCELED -> throw new CannotProcessAnalysisException("분석 요청이 취소된 파일입니다. 관리자에게 문의하세요");
            case PROCESSING -> throw new CannotProcessAnalysisException("이미 분석 중인 파일입니다.");
            case COMPLETED -> throw new CannotProcessAnalysisException("이미 분석된 파일입니다.");
        }
    }
}
