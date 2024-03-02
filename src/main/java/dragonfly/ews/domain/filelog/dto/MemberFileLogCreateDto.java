package dragonfly.ews.domain.filelog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
