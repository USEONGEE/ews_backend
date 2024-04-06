package dragonfly.ews.domain.file.aop.strategy;

import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileContainLogsResponseDto;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileUpdateDto;
import dragonfly.ews.domain.file.exception.ExtensionMismatchException;
import dragonfly.ews.domain.file.exception.FileNotInProjectException;
import dragonfly.ews.domain.file.exception.NoFileNameException;
import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.filelog.repository.ExcelFileColumnRepository;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.file.utils.ExcelFileReader;
import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static dragonfly.ews.domain.file.domain.FileExtension.*;

@Component
@RequiredArgsConstructor
public class ExcelMemberFileStrategy implements MemberFileStrategy {
    private final FileUtils memberFileUtils;
    private final ExcelFileReader excelFileReader;
    private final MemberFileRepository memberFileRepository;
    private final ExcelFileColumnRepository excelFileColumnRepository;
    private final MemberFileLogRepository memberFileLogRepository;

    @Override
    public boolean canSupport(FileExtension fileExtension) {
        return fileExtension == CSV || fileExtension == XLS || fileExtension == XLSX;
    }

    @Override
    public MemberFile createMemberFile(Member owner, MemberFileCreateDto memberFileCreateDto) {
        // 파일명 검증
        String originalFilename = memberFileCreateDto.getFile().getOriginalFilename();
        if (originalFilename.isEmpty()) {
            throw new NoFileNameException("사용자가 제공한 파일에 이름이 없습니다.");
        }
        String savedFilename = memberFileUtils.createSavedFilename(originalFilename);

        // 엔티티 생성
        MemberFile memberFile = new MemberFile(owner,
                memberFileCreateDto.getFileName(),
                memberFileCreateDto.getFile().getOriginalFilename());

        // 로그 생성
        ExcelMemberFileLog excelMemberFileLog = new ExcelMemberFileLog(memberFile, savedFilename);
        excelMemberFileLog.changeDescription(memberFileCreateDto.getDescription());
        
        //
        // ExcelMemberFileLog에 ExcelFileColumn 연결
        List<ExcelFileColumnCreateDto> dtos =
                excelFileReader.extractExcelFileColumnCreateDto(memberFileCreateDto.getFile());
        for (ExcelFileColumnCreateDto dto : dtos) {
            ExcelFileColumn excelFileColumn = new ExcelFileColumn(dto);
            excelMemberFileLog.addColumn(excelFileColumn);
        }
        memberFile.addMemberFileLog(excelMemberFileLog);

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
}

