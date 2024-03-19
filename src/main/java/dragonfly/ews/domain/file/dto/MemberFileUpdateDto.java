package dragonfly.ews.domain.file.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

/**
 * [MemberFile 을 Update하기 위한 Dto]
 */
@Data
@NoArgsConstructor
@ToString
public class MemberFileUpdateDto {
    @NotNull
    private Long memberFileId;
    @NotNull
    private MultipartFile file;
    @NotEmpty
    private String description;
}
