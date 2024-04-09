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
    import dragonfly.ews.domain.filelog.exception.CannotTransformationException;
    import dragonfly.ews.domain.filelog.exception.NoSuchMemberFileLogException;
    import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogRepository;
    import lombok.RequiredArgsConstructor;
    import org.apache.poi.ss.usermodel.*;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Component;
    import org.springframework.transaction.annotation.Transactional;

    import java.io.FileInputStream;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.util.List;

    @Component
    @RequiredArgsConstructor
    @Transactional
    public class XlsxSingleColumnTransformer implements SingleColumnTransformer {
        private final ExcelMemberFileLogRepository excelMemberFileLogRepository;
        private final FileUtils fileUtils;
        private final ExcelFileReader excelFileReader;


        @Value("${file.dir}")
        private String fileDir;

        @Override
        public boolean canSupport(FileExtension fileExtension) {
            return fileExtension == FileExtension.XLSX || fileExtension == FileExtension.XLS;
        }

        @Override
        public void transform(SingleColumnTransformRequestDto dto) throws IOException {
            try {


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

                FileInputStream fileInputStream = new FileInputStream(filePath);
                Workbook workbook = new XSSFWorkbook(fileInputStream);
                Sheet sheet = workbook.getSheetAt(0);

                int columnToTransform = findColumnIndex(sheet, columnName);

                if (columnToTransform != -1) {
                    int lastColumn = sheet.getRow(0).getLastCellNum();
                    // 헤더 행에 새 컬럼 이름 추가
                    Cell headerCell = sheet.getRow(0).createCell(lastColumn);
                    headerCell.setCellValue(columnName + "_" + transformMethod); // 새 컬럼의 이름을 설정
                    for (Row row : sheet) {
                        if (row.getRowNum() == 0) continue; // 헤더 행은 건너뛰기
                        Cell sourceCell = row.getCell(columnToTransform);
                        Cell newCell = row.createCell(lastColumn, sourceCell.getCellType());

                        if (sourceCell != null && sourceCell.getCellType() == CellType.NUMERIC) {
                            double currentValue = sourceCell.getNumericCellValue();
                            double transformedValue = applyTransformation(currentValue, transformMethod);
                            newCell.setCellValue(transformedValue);
                        } else {
                            newCell.setCellValue("N/A"); // 숫자가 아닐 경우 처리
                        }
                    }

                    // 파일 저장
                    try (FileOutputStream fileOutputStream = new FileOutputStream(fileDir + savedFilename)) {
                        workbook.write(fileOutputStream);
                    }
                } else {
                    throw new IllegalStateException();
                }

                workbook.close();
                fileInputStream.close();

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
            catch (Exception e) {
                throw new CannotTransformationException("Transformation failed.");
            }
        }

        private static int findColumnIndex(Sheet sheet, String columnName) {
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().equalsIgnoreCase(columnName)) {
                    return cell.getColumnIndex();
                }
            }
            return -1;
        }

        private static double applyTransformation(double value, SingleColumnTransformMethod method) {
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

