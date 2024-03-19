package dragonfly.ews.domain.filelog.dto;

import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelMemberFileLogResponseDto extends MemberFileLogResponseDto {

    private List<ExcelFileColumResponseDto> columns = new ArrayList<>();

    public ExcelMemberFileLogResponseDto(ExcelMemberFileLog excelMemberFileLog) {
        super(excelMemberFileLog);
        List<ExcelFileColumn> excelFileColumns = excelMemberFileLog.getColumns();
        for (ExcelFileColumn excelFileColumn : excelFileColumns) {
            columns.add(new ExcelFileColumResponseDto(excelFileColumn));
        }
    }

    @Data
    @AllArgsConstructor
    private static class ExcelFileColumResponseDto {
        private Long id;
        private String columnName;
        private String dataType;

        public ExcelFileColumResponseDto(ExcelFileColumn excelFileColumn) {
            this(excelFileColumn.getId(), excelFileColumn.getColumnName(), excelFileColumn.getDataType());
        }
    }
}
