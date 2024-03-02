package dragonfly.ews.domain.file.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MemberFileCreateDto {
    private MultipartFile file;
    private String fileName;
    private String description;
}
