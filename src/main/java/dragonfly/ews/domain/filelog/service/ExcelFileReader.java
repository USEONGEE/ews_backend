package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.filelog.controller.ExcelDataDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelFileReader implements FileReader<ExcelDataDto> {
    @Override
    public ExcelDataDto read(String filePath) {
        ExcelDataDto excelDataDto = new ExcelDataDto();
        List<String> columnNames = new ArrayList<>();
        List<ExcelDataDto.ExcelRowDataDto> rows = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;

            for (Row row : sheet) {
                if (row.getRowNum() >= 30) break; // 상위 30열까지만 읽음

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        excelDataDto.setColumnNames(columnNames);
        if (!columnNames.isEmpty()) { // 첫 번째 행이 처리되면, 다음 행부터 데이터로 취급
            excelDataDto.setRows(rows);
        }
        return excelDataDto;
    }
}
