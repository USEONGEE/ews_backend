package dragonfly.ews.domain.result.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserAnalysisRequestDto {
    @NotNull
    private Long memberFileLogId;
    @NotNull
    private boolean all;
    @Size(min = 1, message = "selected ColumnIds must have at least one element")
    private List<Long> selectedColumnIds;
    @Size(min = 1, message = "selected ColumnIds must have at least one element")
    private List<Long> targetColumnIds;
    @NotEmpty
    private String description;
}
