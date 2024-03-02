package dragonfly.ews.domain.file.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MemberFileUpdateDto {
    private Long memberFileId;
    private MultipartFile file;
    private String description;
}
