package dragonfly.ews.domain.filelog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data@NoArgsConstructor
public class ExcelDataDto {
    private List<String> columnNames; // 열 이름
    private List<ExcelRowDataDto> rows; // 각 행의 데이터
    @Data
    public static class ExcelRowDataDto {
        private List<String> data;
    }
}
