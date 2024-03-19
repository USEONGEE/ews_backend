package dragonfly.ews.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [ExcelFileColumn Entity를 생성하기 위한 DTO]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFileColumnCreateDto {
    private String columnName;
    private String dataType;
}
