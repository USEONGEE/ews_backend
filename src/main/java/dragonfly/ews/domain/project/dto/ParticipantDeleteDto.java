package dragonfly.ews.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDeleteDto {
    private Long projectId;
    private Long projectParticipantId;
}
