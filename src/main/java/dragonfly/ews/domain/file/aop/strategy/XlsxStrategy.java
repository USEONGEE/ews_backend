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
import dragonfly.ews.domain.file.repository.ExcelMemberFileRepositoryApi;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * xls 포함
 */
@Component
@RequiredArgsConstructor
public class XlsxStrategy implements MemberFileStrategy {

    private final DataTypeProvider dataTypeProvider;
    private final FileUtils memberFileUtils;
    private final ExcelFileColumnRepository excelFileColumnRepository;
    private final ExcelMemberFileRepository excelMemberFileRepository;
    private final MemberFileLogRepository memberFileLogRepository;

    public List<ExcelFileColumnCreateDto> savePreProcess(MultipartFile multipartFile) {
        try {
            return processExcelFile(multipartFile.getInputStream());
        } catch (Exception e) {
            throw new FilePostProcessException(e);
        }
    }

    private List<ExcelFileColumnCreateDto> processExcelFile(InputStream inputStream) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        List<String> columnNames = new ArrayList<>();
        List<String> dataTypes = new ArrayList<>();

        if (rowIterator.hasNext()) {
            Row headerRow = rowIterator.next();
            headerRow.forEach(cell -> columnNames.add(cell.getStringCellValue()));
        }

        if (rowIterator.hasNext()) {
            Row firstRow = rowIterator.next();
            firstRow.forEach(cell -> {
                String data = cell.toString();
                String dataType = dataTypeProvider.determineDataType(data);
                dataTypes.add(dataType);
            });
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
        workbook.close();
        return columnCreateDtos;
    }

    @Override
    public boolean canSupport(FileExtension fileExtension) {
        return fileExtension == FileExtension.XLSX || fileExtension == FileExtension.XLS;
    }

    @Override
    public MemberFile createMemberFile(Member owner, MemberFileCreateDto memberFileCreateDto) {
        //
        List<ExcelFileColumnCreateDto> dtos = savePreProcess(memberFileCreateDto.getFile());
        // 파일명 검증
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
