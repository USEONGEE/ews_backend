package dragonfly.ews.domain.file.dto;

import dragonfly.ews.domain.file.domain.MemberFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFileResponseDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String description;
    private LocalDateTime createdDate;

    public static MemberFileResponseDto of(MemberFile memberFile) {
        return MemberFileResponseDto.builder()
                .id(memberFile.getId())
                .fileName(memberFile.getFileName())
                .fileType(memberFile.getFileExtension())
                .createdDate(memberFile.getCreatedDate())
                .description(memberFile.getDescription())
                .build();
    }

}
