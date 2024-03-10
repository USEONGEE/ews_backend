package dragonfly.ews.domain.result.dto;

import dragonfly.ews.domain.file.domain.FileExtension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequestDto {
    private FileExtension fileExtension;
    private List<AnalysisExcelFileColumnDto> columns;
    private boolean all;
}
