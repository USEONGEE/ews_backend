package dragonfly.ews.domain.project.dto;

import dragonfly.ews.domain.member.dto.MemberResponseDto;
import dragonfly.ews.domain.project.domain.ProjectParticipant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ProjectParticipantResponseDto {
    private Long id;
    private MemberResponseDto memberResponseDto;

    public static ProjectParticipantResponseDto of(ProjectParticipant projectParticipant) {
        MemberResponseDto dto = MemberResponseDto.of(projectParticipant.getMember());
        return ProjectParticipantResponseDto.builder()
                .id(projectParticipant.getId())
                .memberResponseDto(dto)
                .build();
    }

}
