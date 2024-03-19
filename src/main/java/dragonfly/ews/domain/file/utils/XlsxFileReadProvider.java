package dragonfly.ews.domain.file.utils;

import dragonfly.ews.domain.file.aop.utils.DataTypeProvider;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.file.exception.CannotSaveFileException;
import dragonfly.ews.domain.filelog.dto.ExcelDataDto;
import dragonfly.ews.domain.filelog.exception.CannotResolveFileReadException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static dragonfly.ews.domain.file.domain.FileExtension.*;

// TODO 예외처리 바꾸기
@Component
@RequiredArgsConstructor
public class XlsxFileReadProvider implements ExcelFileReadProvider {
    private final DataTypeProvider dataTypeProvider;
    @Override
    public boolean support(FileExtension extension) {
        return extension == XLSX || extension == XLS;
    }

    @Override
    public ExcelDataDto read(String filePath) {
        ExcelDataDto excelDataDto = new ExcelDataDto();
        List<String> columnNames = new ArrayList<>();
        List<ExcelDataDto.ExcelRowDataDto> rows = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (row.getRowNum() >= 20) break; // 상위 30열까지만 읽음

                ExcelDataDto.ExcelRowDataDto rowDataDto = new ExcelDataDto.ExcelRowDataDto();
                List<String> rowData = new ArrayList<>();

                for (Cell cell : row) {
                    if (isFirstRow) {
                        // 첫 번째 행은 열 이름으로 처리
                        columnNames.add(cell.getStringCellValue());
                    } else {
                        // 데이터 셀 값을 문자열로 변환
                        switch (cell.getCellType()) {
                            case STRING:
                                rowData.add(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    rowData.add(cell.getDateCellValue().toString());
                                } else {
                                    rowData.add(String.valueOf(cell.getNumericCellValue()));
                                }
                                break;
                            case BOOLEAN:
                                rowData.add(String.valueOf(cell.getBooleanCellValue()));
                                break;
                            case FORMULA:
                                rowData.add(cell.getCellFormula());
                                break;
                            default:
                                rowData.add(" ");
                        }
                    }
                }
                if (!isFirstRow) {
                    rowDataDto.setData(rowData);
                    rows.add(rowDataDto);
                }
                isFirstRow = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        excelDataDto.setColumnNames(columnNames);
        if (!columnNames.isEmpty()) {
            excelDataDto.setRows(rows);
        }
        return excelDataDto;
    }

    @Override
    public List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDtos(MultipartFile multipartFile) {
        try {
            InputStream inputStream = multipartFile.getInputStream();
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
        } catch (Exception e) {
            throw new CannotResolveFileReadException("파일을 읽을 수 없습니다.");
        }
    }
}
