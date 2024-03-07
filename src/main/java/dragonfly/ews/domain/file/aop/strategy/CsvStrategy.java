package dragonfly.ews.domain.file.aop.strategy;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.aop.utils.DataTypeProvider;
import dragonfly.ews.domain.file.domain.ExcelFileColumn;
import dragonfly.ews.domain.file.domain.ExcelMemberFile;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.file.dto.ExcelMemberFileContainLogsResponseDto;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.exception.*;
import dragonfly.ews.domain.file.repository.ExcelFileColumnRepository;
import dragonfly.ews.domain.file.repository.ExcelMemberFileRepository;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CsvStrategy implements MemberFileStrategy {
    private final DataTypeProvider dataTypeProvider;
    private final FileUtils memberFileUtils;
    private final ExcelFileColumnRepository excelFileColumnRepository;
    private final ExcelMemberFileRepository excelMemberFileRepository;
    private final MemberFileLogRepository memberFileLogRepository;

    public List<ExcelFileColumnCreateDto> savePreProcess(MultipartFile multipartFile) {
        try {
            return processCsvFile(multipartFile.getInputStream());
        } catch (Exception e) {
            throw new FilePostProcessException(e);
        }
    }


    private List<ExcelFileColumnCreateDto> processCsvFile(InputStream inputStream) throws Exception {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = fileReader.readLine(); // 첫 번째 줄을 읽어 컬럼 이름을 추출합니다.
            if (headerLine != null) {
                List<String> columnNames = Arrays.asList(headerLine.split(","));
                List<String> dataTypes = new ArrayList<>();

                String firstDataRow = fileReader.readLine(); // 두 번째 줄을 읽어 첫 번째 데이터 행을 추출합니다.
                if (firstDataRow != null) {
                    List<String> dataValues = Arrays.asList(firstDataRow.split(","));
                    for (String data : dataValues) {
                        String dataType = dataTypeProvider.determineDataType(data);
                        dataValues.add(dataType);
                    }

                    System.out.println("Data Types of the first row: " + dataTypes);
                }
                List<ExcelFileColumnCreateDto> columnCreateDtos = new ArrayList<>();
                for (int i = 0; i < columnNames.size(); i++) {
                    String columnName = columnNames.get(i);
                    String dataType = i < dataTypes.size() ? dataTypes.get(i) : "String"; // dataTypes 리스트가 짧을 경우 기본값으로 "String" 사용
                    ExcelFileColumnCreateDto dto = new ExcelFileColumnCreateDto(columnName, dataType);
                    columnCreateDtos.add(dto);
                }
                if (columnNames.size() != columnCreateDtos.size()) {
                    throw new CannotSaveFileException("파일의 데이터가 올바르지 않습니다.");
                }
                return columnCreateDtos;
            } else {
                throw new CannotSaveFileException("파일의 데이터가 올바르지 않습니다.");
            }
        }
    }

    @Override
    public boolean canSupport(FileExtension fileExtension) {
        return fileExtension == FileExtension.CSV;
    }

    @Override
    public MemberFile createMemberFile(Member owner, MemberFileCreateDto memberFileCreateDto) {
        List<ExcelFileColumnCreateDto> dtos = savePreProcess(memberFileCreateDto.getFile());

        String originalFilename = memberFileCreateDto.getFile().getOriginalFilename();
        hasName(originalFilename);
        String savedFilename = memberFileUtils.createSavedFilename(originalFilename);

        // 엔티티 생성
        ExcelMemberFile excelMemberFile = new ExcelMemberFile(owner,
                memberFileCreateDto.getFileName(),
                memberFileCreateDto.getFile().getOriginalFilename(),
                savedFilename);

        // column 연결
        for (ExcelFileColumnCreateDto excelFileColumnCreateDto : dtos) {
            ExcelFileColumn excelFileColumn = new ExcelFileColumn(excelFileColumnCreateDto);
            excelMemberFile.addColumn(excelFileColumn);
        }
        memberFileUtils.storeFile(memberFileCreateDto.getFile(), savedFilename);

        // ThreadLocal 정리
        return excelMemberFile;
    }

    private void hasName(String fileName) {
        if (fileName.isEmpty()) {
            throw new NoFileNameException("사용자가 제공한 파일에 이름이 없습니다.");
        }
    }

    @Override
    public void updateValidate(MemberFile memberFile, MultipartFile target) {
        hasProject(memberFile);
        // 파일 확장자가 같은지
        String fileExt = memberFileUtils.getFileExt(target.getOriginalFilename());
        checkFileExtension(memberFile, fileExt);
        // TODO 파일 column이 같은지 + 데이터 타입이 같은지
    }

    // TODO 총 3번의 쿼리가 나간다. 수정 필요, 2번의 컬렉션 페치 조인을 해결해야함
    @Override
    public Object findDtoById(Long memberId, Long memberFileId) {
        ExcelMemberFile excelMemberFile = excelMemberFileRepository.findById(memberFileId)
                .orElseThrow(NoSuchFileException::new);
        List<ExcelFileColumn> excelFileColumns = excelFileColumnRepository.findByExcelMemberFileId(memberFileId);
        List<MemberFileLog> memberFileLogs = memberFileLogRepository.findByMemberFileId(memberFileId);
        excelMemberFile.injectMemberFileLogs(memberFileLogs);
        excelMemberFile.injectExcelFileColumns(excelFileColumns);
        return new ExcelMemberFileContainLogsResponseDto(excelMemberFile);
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
}
