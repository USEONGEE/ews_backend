package dragonfly.ews.domain.file.dto;

import dragonfly.ews.domain.file.domain.MemberFile;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MemberFileResponseDto {
    private Long id;
    private String fileName;
    private String fileType;
    private LocalDateTime createdDate;

    public MemberFileResponseDto(MemberFile memberFile) {
        this.id = memberFile.getId();
        this.fileName = memberFile.getFileName();
        this.fileType = memberFile.getFileType();
        this.createdDate = memberFile.getCreatedDate();
    }
}
