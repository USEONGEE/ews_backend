package dragonfly.ews.domain.file.dto;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberFileContainLogsResponseDto {
    private Long id;
    private String fileName;
    private String fileType;
    private LocalDateTime createdDate;
    private List<MemberFileLogResponseDto> fileLogs = new ArrayList<>();

    public MemberFileContainLogsResponseDto(MemberFile memberFile) {
        this.id = memberFile.getId();
        this.createdDate = memberFile.getCreatedDate();
        this.fileName = memberFile.getFileName();
        this.fileType = memberFile.getFileType();
        for (MemberFileLog memberFileLog : memberFile.getMemberFileLogs()) {
            this.fileLogs.add(new MemberFileLogResponseDto(memberFileLog));
        }
    }

    @Data
    private static class MemberFileLogResponseDto {
        private Long id;
        private LocalDateTime createdDate;

        public MemberFileLogResponseDto(MemberFileLog memberFileLog) {
            this.id = memberFileLog.getId();
            this.createdDate = memberFileLog.getCreatedDate();
        }
    }
}

