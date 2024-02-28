package dragonfly.ews.domain.file.dto;

import dragonfly.ews.domain.file.domain.MemberFile;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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
