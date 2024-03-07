package dragonfly.ews.domain.file.dto;

import dragonfly.ews.domain.file.domain.ExcelMemberFile;
import dragonfly.ews.domain.file.domain.MemberFile;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ExcelMemberFileContainLogsResponseDto extends MemberFileContainLogsResponseDto {
    private List<ExcelFileColumnResponseDto> columns = new ArrayList<>();

    public ExcelMemberFileContainLogsResponseDto(ExcelMemberFile excelMemberFile) {
        super((MemberFile) excelMemberFile);
        List<ExcelFileColumnResponseDto> list = excelMemberFile
                .getColumns()
                .stream()
                .map(ExcelFileColumnResponseDto::of)
                .toList();
        for (ExcelFileColumnResponseDto excelFileColumnResponseDto : list) {
            columns.add(excelFileColumnResponseDto);
        }
    }
}
