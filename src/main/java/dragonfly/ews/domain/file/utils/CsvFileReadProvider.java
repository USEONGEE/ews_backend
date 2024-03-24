package dragonfly.ews.domain.file.utils;

import dragonfly.ews.domain.file.aop.utils.DataTypeProvider;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.file.exception.CannotSaveFileException;
import dragonfly.ews.domain.filelog.dto.ExcelDataDto;
import dragonfly.ews.domain.filelog.exception.CannotResolveFileReadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CsvFileReadProvider implements ExcelFileReadProvider {
    private final DataTypeProvider dataTypeProvider;

    @Override
    public boolean support(FileExtension extension) {
        return extension == FileExtension.CSV;
    }

    @Override
    public ExcelDataDto read(String filePath) {
        ExcelDataDto excelDataDto = new ExcelDataDto();
        List<String> columnNames = new ArrayList<>();
        List<ExcelDataDto.ExcelRowDataDto> rows = new ArrayList<>();
        int rowCount = 0; // 행을 세기 위한 변수

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            boolean isFirstRow = true;
            while ((line = br.readLine()) != null && rowCount < 20) { // 조건에 rowCount < 20 추가
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
                    rowCount++; // 행을 세는 변수를 증가
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        excelDataDto.setColumnNames(columnNames);
        excelDataDto.setRows(rows);
        return excelDataDto;
    }


    @Override
    public List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDtos(MultipartFile multipartFile) {
        try {
            InputStream inputStream = multipartFile.getInputStream();
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String headerLine = fileReader.readLine(); // 첫 번째 줄을 읽어 컬럼 이름을 추출합니다.
            if (headerLine != null) {
                List<String> columnNames = Arrays.asList(headerLine.split(","));
                List<String> dataTypes = new ArrayList<>(); // 데이터 타입을 저장할 리스트를 초기화합니다. (변경되지 않음)

                String firstDataRow = fileReader.readLine(); // 두 번째 줄을 읽어 첫 번째 데이터 행을 추출합니다.
                if (firstDataRow != null) {
                    List<String> dataValues = new ArrayList<>(Arrays.asList(firstDataRow.split(","))); // 수정된 부분: 수정 가능한 리스트로 변경
                    for (String data : dataValues) {
                        String dataType = dataTypeProvider.determineDataType(data);
                        dataTypes.add(dataType); // 수정된 부분: 올바른 리스트에 데이터 타입 추가
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
        } catch (Exception e) {
            throw new CannotResolveFileReadException(e);
        }
    }

    @Override
    public List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDtos(String filePath) {
        List<ExcelFileColumnCreateDto> columnCreateDtos = new ArrayList<>();
        try (BufferedReader fileReader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String headerLine = fileReader.readLine(); // 첫 번째 줄을 읽어 컬럼 이름을 추출합니다.
            if (headerLine != null) {
                List<String> columnNames = Arrays.asList(headerLine.split(","));
                List<String> dataTypes = new ArrayList<>(); // 데이터 타입을 저장할 리스트를 초기화합니다.

                String firstDataRow = fileReader.readLine(); // 두 번째 줄을 읽어 첫 번째 데이터 행을 추출합니다.
                if (firstDataRow != null) {
                    List<String> dataValues = new ArrayList<>(Arrays.asList(firstDataRow.split(",")));
                    for (String data : dataValues) {
                        String dataType = dataTypeProvider.determineDataType(data);
                        dataTypes.add(dataType);
                    }

                    System.out.println("Data Types of the first row: " + dataTypes);
                }
                for (int i = 0; i < columnNames.size(); i++) {
                    String columnName = columnNames.get(i);
                    String dataType = i < dataTypes.size() ? dataTypes.get(i) : "String"; // dataTypes 리스트가 짧을 경우 기본값으로 "String" 사용
                    columnCreateDtos.add(new ExcelFileColumnCreateDto(columnName, dataType));
                }
            } else {
                throw new CannotSaveFileException("파일의 데이터가 올바르지 않습니다.");
            }
        } catch (IOException e) {
            throw new CannotResolveFileReadException(e);
        }
        return columnCreateDtos;
    }
}
