package dragonfly.ews.domain.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ParticipantCreateDto {
    public Long projectId;
    public List<ParticipantDto> participantIds;
}
