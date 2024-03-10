package dragonfly.ews.domain.result.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnalysisRequestDto {
    private Long memberFileLodId;
    private boolean all;
    private List<Long> selectedColumnIds;

}
