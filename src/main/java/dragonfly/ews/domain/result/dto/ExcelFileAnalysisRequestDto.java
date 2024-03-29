package dragonfly.ews.domain.result.dto;

import dragonfly.ews.domain.file.domain.FileExtension;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExcelFileAnalysisRequestDto {
    private FileExtension fileExtension;
    private String callbackUrl;
    private String redisKey;
    private List<AnalysisExcelFileColumnDto> columns = new ArrayList<>();
    private List<AnalysisExcelFileColumnDto> targetColumns = new ArrayList<>();

    public void addColumns(AnalysisExcelFileColumnDto analysisExcelFileColumnDto) {
        this.columns.add(analysisExcelFileColumnDto);
    }

    public void addTargetColumns(AnalysisExcelFileColumnDto analysisExcelFileColumnDto) {
        this.targetColumns.add(analysisExcelFileColumnDto);
    }
}
