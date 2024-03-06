package dragonfly.ews.domain.filelog.dto;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data@NoArgsConstructor
public class MemberFileLogResponseDto {
    private Long id;
    private LocalDateTime createdDate;
    private String description;
    private boolean validated;
    private String fileName;

    public MemberFileLogResponseDto(MemberFileLog memberFileLog) {
        this.id = memberFileLog.getId();
        this.createdDate = memberFileLog.getCreatedDate();
        this.description = memberFileLog.getDescription();
        this.validated = memberFileLog.isValidated();
        this.fileName = memberFileLog.getMemberFile().getFileName();
    }
}
