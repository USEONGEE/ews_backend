package dragonfly.ews.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFileColumnCreateDto {
    private String columnName;
    private String dataType;
}
