package dragonfly.ews.domain.filelog.dto;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberFileLogResponseDto {
    private Long id;
    private LocalDateTime createdDate;
    private String description;

    public MemberFileLogResponseDto(MemberFileLog memberFileLog) {
        this.id = memberFileLog.getId();
        this.createdDate = memberFileLog.getCreatedDate();
        this.description = memberFileLog.getDescription();
    }
}
