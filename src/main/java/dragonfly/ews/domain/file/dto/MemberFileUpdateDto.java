package dragonfly.ews.domain.file.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@ToString
public class MemberFileUpdateDto {
    private Long memberFileId;
    private MultipartFile file;
    private String description;
}
