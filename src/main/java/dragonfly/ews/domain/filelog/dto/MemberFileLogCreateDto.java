package dragonfly.ews.domain.filelog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberFileLogCreateDto {
    private String savedFileName;
    private String description;

    public static MemberFileLogCreateDto of(String savedFileName, String description) {
        return MemberFileLogCreateDto.builder()
                .savedFileName(savedFileName)
                .description(description)
                .build();
    }

}
