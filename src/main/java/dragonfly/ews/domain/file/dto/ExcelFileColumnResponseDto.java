package dragonfly.ews.domain.file.dto;

import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [ExcelMemberFilelog 가 가진 Column을 반환하기 위한 DTO]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelFileColumnResponseDto {
    private Long id;
    private String columnName;
    private String dataType;
    private String description;

    public static ExcelFileColumnResponseDto of(ExcelFileColumn excelFileColumn) {
        return ExcelFileColumnResponseDto.builder()
                .id(excelFileColumn.getId())
                .columnName(excelFileColumn.getColumnName())
                .dataType(excelFileColumn.getDataType())
                .description(excelFileColumn.getDescription())
                .build();
    }
}
