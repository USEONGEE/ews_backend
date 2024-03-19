package dragonfly.ews.domain.result.dto;

import dragonfly.ews.domain.file.domain.FileExtension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFileAnalysisRequestDto {
    private FileExtension fileExtension;
    private List<AnalysisExcelFileColumnDto> columns = new ArrayList<>();
    private List<AnalysisExcelFileColumnDto> targetColumns = new ArrayList<>();
    private boolean all;

    // 임시
    public ExcelFileAnalysisRequestDto(FileExtension fileExtension, List<AnalysisExcelFileColumnDto> columns, boolean all) {
        this.fileExtension = fileExtension;
        this.columns = columns;
        this.all = all;
    }

    public ExcelFileAnalysisRequestDto(FileExtension fileExtension, boolean all) {
        this.fileExtension = fileExtension;
        this.all = all;
    }

    public void addColumns(AnalysisExcelFileColumnDto analysisExcelFileColumnDto) {
        this.columns.add(analysisExcelFileColumnDto);
    }

    public void addTargetColumns(AnalysisExcelFileColumnDto analysisExcelFileColumnDto) {
        this.columns.add(analysisExcelFileColumnDto);
    }
}
