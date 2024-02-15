package dragonfly.ews.domain.file.domain;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberFileResponseDto {
    public Long id;
    public String createBy;
    public LocalDateTime createdDate;

    public MemberFileResponseDto(MemberFileLog memberFileLog) {
        this.id = memberFileLog.getId();
        this.createBy = memberFileLog.getCreatedBy();
        this.createdDate = memberFileLog.getCreatedDate();
    }
}

