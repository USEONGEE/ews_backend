package dragonfly.ews.domain.file.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * [MemberFile Entity 생성]
 */
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class MemberFileCreateDto {
    private MultipartFile file;
    private String fileName;
    private String description;
    private Long projectId;

}
