package dragonfly.ews.domain.result.dto;

import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisExcelFileColumnDto {
    private String columnName;
    private String dataType;

    public static AnalysisExcelFileColumnDto of(ExcelFileColumn excelFileColumn) {
        return AnalysisExcelFileColumnDto.builder()
                .columnName(excelFileColumn.getColumnName())
                .dataType(excelFileColumn.getDataType())
                .build();
    }
}
