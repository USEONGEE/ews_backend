package dragonfly.ews.domain.filelog.util;

import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.file.utils.ExcelFileReader;
import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import dragonfly.ews.domain.filelog.domain.SingleColumnTransformMethod;
import dragonfly.ews.domain.filelog.dto.SingleColumnTransformRequestDto;
import dragonfly.ews.domain.filelog.exception.NoSuchMemberFileLogException;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class CsvSingleColumnTransformer implements SingleColumnTransformer {
    private final ExcelMemberFileLogRepository excelMemberFileLogRepository;
    private final FileUtils fileUtils;
    private final ExcelFileReader excelFileReader;
    @Override
    public boolean canSupport(FileExtension fileExtension) {
        return fileExtension == FileExtension.CSV;
    }

    @Override
    public void transform(SingleColumnTransformRequestDto dto) throws IOException {
        ExcelMemberFileLog excelMemberFileLog = excelMemberFileLogRepository.findByIdContainColumn(dto.getMemberFileLogId())
                .orElseThrow(NoSuchMemberFileLogException::new);
        MemberFile memberFile = excelMemberFileLog.getMemberFile();

        // 파일 불러오기
        String savedName = excelMemberFileLog.getSavedName();
        String filePath = fileUtils.getFullPath(savedName);

        // 변환 메소드 추출
        SingleColumnTransformMethod transformMethod = dto.getMethod();

        // 대상 컬럼 추출
        ExcelFileColumn excelFileColumn = excelMemberFileLog.getColumns()
                .stream()
                .filter(column -> column.getId().equals(dto.getColumnId()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        String columnName = excelFileColumn.getColumnName();

        // 저장될 파일 이름
        String savedFilename = fileUtils.createSavedFilename(savedName);


        List<String> headers = new ArrayList<>();
        List<List<String>> newRows = new ArrayList<>();
        int columnToTransform = -1;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            if (line != null) {
                headers.addAll(Arrays.asList(line.split(",")));
                columnToTransform = headers.indexOf(columnName);
                headers.add(columnName + "_" + transformMethod); // 변환된 컬럼에 새 이름 추가
            }

            while ((line = br.readLine()) != null) {
                List<String> rowValues = new ArrayList<>(Arrays.asList(line.split(",")));
                if (columnToTransform != -1 && columnToTransform < rowValues.size()) {
                    String originalValue = rowValues.get(columnToTransform);
                    try {
                        double value = Double.parseDouble(originalValue);
                        double transformedValue = applyTransformation(value, transformMethod);
                        rowValues.add(String.valueOf(transformedValue));
                    } catch (NumberFormatException e) {
                        rowValues.add("N/A");
                    }
                } else {
                    rowValues.add("N/A");
                }
                newRows.add(rowValues);
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileUtils.getFullPath(savedFilename)), StandardCharsets.UTF_8))) {
            bw.write(String.join(",", headers));
            bw.newLine();
            for (List<String> row : newRows) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }

        // 로그 생성
        ExcelMemberFileLog newMemberFileLog = new ExcelMemberFileLog(memberFile, savedFilename);
        newMemberFileLog.changeDescription(dto.getDescription());

        // 파일 업데이트
        List<ExcelFileColumnCreateDto> dtos
                = excelFileReader.extractExcelFileColumnCreateDto(fileUtils.getFullPath(savedFilename));
        for (ExcelFileColumnCreateDto excelFileColumnCreateDto : dtos) {
            ExcelFileColumn newExcelFileColumn = new ExcelFileColumn(excelFileColumnCreateDto);
            newMemberFileLog.addColumn(newExcelFileColumn);
        }
        memberFile.addMemberFileLog(newMemberFileLog);
    }

    private double applyTransformation(double value, SingleColumnTransformMethod method) {
        switch (method) {
            case SQUARE:
                return Math.pow(value, 2);
            case SQUARE_ROOT:
                return Math.sqrt(value);
            case COMMERCIAL_LOG:
                return Math.log10(value);
            case NATURAL_LOG:
                return Math.log(value);
            default:
                throw new IllegalArgumentException("Unsupported transformation method.");
        }
    }
}
