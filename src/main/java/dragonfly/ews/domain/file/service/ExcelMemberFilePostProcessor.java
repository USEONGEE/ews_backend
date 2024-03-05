package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.domain.ExcelFileColumn;
import dragonfly.ews.domain.file.domain.ExcelMemberFile;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.file.exception.CannotSaveFileException;
import dragonfly.ews.domain.file.exception.FilePostProcessException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Excel 파일에 대하여 첫 컬럼을 읽고 ExcelFileColumn 을 생성 후 저장
 */
@Component
@RequiredArgsConstructor
@Transactional
public class ExcelMemberFilePostProcessor implements MemberFilePostProcessor<ExcelMemberFile> {

    @Override
    public boolean canProcess(String extension) {
        return extension.equalsIgnoreCase("csv") || extension.equalsIgnoreCase("xlsx")
                || extension.equalsIgnoreCase("xls");
    }

    @Override
    public void process(MemberFile target, MultipartFile multipartFile) {
        List<ExcelFileColumnCreateDto> dtos = null;
        try {
            String fileType = target.getFileExtension();
            if (fileType.equalsIgnoreCase(".csv")) {
                dtos = processCsvFile(multipartFile.getInputStream());
            } else if (fileType.equalsIgnoreCase("xls") || fileType.equalsIgnoreCase("xlsx")) {
                dtos = processExcelFile(multipartFile.getInputStream());
            }
            saveMemberFile((ExcelMemberFile) target, dtos);
        } catch (Exception e) {
            throw new FilePostProcessException(e);
        }
    }

    // TODO 메소드 이름 바꾸기
    private void saveMemberFile(ExcelMemberFile memberFile, List<ExcelFileColumnCreateDto> dtos) {
        for (ExcelFileColumnCreateDto dto : dtos) {
            memberFile.addColumn(new ExcelFileColumn(memberFile, dto));
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
                        String dataType = determineDataType(data);
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
                String dataType = determineDataType(data);
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

    private String determineDataType(String data) {
        if (data.matches("-?\\d+")) {
            return "Integer";
        } else if (data.matches("-?\\d+(\\.\\d+)?")) {
            return "Double";
        } else if (tryParseDate(data)) {
            return "Date";
        } else {
            return "String";
        }
    }

    private boolean tryParseDate(String data) {
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate.parse(data, formatter);
                return true;
            } catch (DateTimeParseException e) {
                try {
                    LocalDateTime.parse(data, formatter);
                    return true;
                } catch (DateTimeParseException ex) {
                    // Try next format
                }
            }
        }
        return false;
    }


}
