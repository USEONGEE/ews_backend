package dragonfly.ews.domain.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AddParticipantsDto {
    public Long projectId;
    public List<ParticipantDto> participantIds;
}
