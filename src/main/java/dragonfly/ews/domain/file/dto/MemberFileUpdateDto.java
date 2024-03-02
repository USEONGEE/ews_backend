package dragonfly.ews.domain.file.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class MemberFileUpdateDto {
    private Long memberFileId;
    private MultipartFile file;
    private String description;
}
