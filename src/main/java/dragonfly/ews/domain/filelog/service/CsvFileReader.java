package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.filelog.controller.ExcelDataDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO 예외처리 바꾸기
@Component
public class CsvFileReader implements FileReader<ExcelDataDto> {

    @Override
    public boolean support(String extension) {
        return extension.equalsIgnoreCase("csv");
    }

    @Override
    public ExcelDataDto read(String filePath) {
        ExcelDataDto excelDataDto = new ExcelDataDto();
        List<String> columnNames = new ArrayList<>();
        List<ExcelDataDto.ExcelRowDataDto> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            boolean isFirstRow = true;
            while ((line = br.readLine()) != null) {
                // CSV 파일의 각 행을 쉼표로 분리
                List<String> values = Arrays.asList(line.split(","));

                if (isFirstRow) {
                    // 첫 번째 행은 열 이름으로 처리
                    columnNames.addAll(values);
                    isFirstRow = false;
                } else {
                    // 데이터 행 처리
                    ExcelDataDto.ExcelRowDataDto rowDataDto = new ExcelDataDto.ExcelRowDataDto();
                    rowDataDto.setData(new ArrayList<>(values)); // 새 리스트로 감싸서 변경 불가능한 리스트 문제 해결
                    rows.add(rowDataDto);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        excelDataDto.setColumnNames(columnNames);
        excelDataDto.setRows(rows);
        return excelDataDto;
    }
}
