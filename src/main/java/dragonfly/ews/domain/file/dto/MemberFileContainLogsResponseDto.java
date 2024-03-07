package dragonfly.ews.domain.file.dto;

import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.dto.MemberFileLogResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MemberFileContainLogsResponseDto {
    private Long id;
    private String fileName;
    private FileExtension fileExtension;
    private LocalDateTime createdDate;
    private List<MemberFileLogResponseDto> fileLogs = new ArrayList<>();


    public MemberFileContainLogsResponseDto(MemberFile memberFile) {
        this.id = memberFile.getId();
        this.createdDate = memberFile.getCreatedDate();
        this.fileName = memberFile.getFileName();
        this.fileExtension = memberFile.getFileExtension();
        for (MemberFileLog memberFileLog : memberFile.getMemberFileLogs()) {
            this.fileLogs.add(new MemberFileLogResponseDto(memberFileLog));
        }
    }
}

