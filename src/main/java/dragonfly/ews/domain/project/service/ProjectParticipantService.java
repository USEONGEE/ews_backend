package dragonfly.ews.domain.project.service;

import dragonfly.ews.domain.project.domain.ProjectParticipant;
import dragonfly.ews.domain.project.dto.ParticipantDeleteDto;
import dragonfly.ews.domain.project.exception.CannotDeleteException;
import dragonfly.ews.domain.project.exception.NoSuchProjectException;
import dragonfly.ews.domain.project.repository.ProjectParticipantRepository;
import dragonfly.ews.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectParticipantService {
    private final ProjectRepository projectRepository;
    private final ProjectParticipantRepository projectParticipantRepository;

    public List<ProjectParticipant> findParticipatesContainMember(Long memberId, Long projectId) {
        projectRepository.findByIdAuth(memberId, projectId)
                .orElseThrow(NoSuchProjectException::new);
        return projectParticipantRepository.findByProjectIdContainMember(projectId);
    }

    public boolean deleteOne(Long memberId, ParticipantDeleteDto participantDeleteDto) {
        // 권한 조회
        projectRepository.findByIdAuth(memberId, participantDeleteDto.getProjectId())
                .orElseThrow(NoSuchProjectException::new);
        // TODO 아래 코드 마무리하기
        // 삭제
        if (!projectParticipantRepository.deleteByIdAndProjectId(participantDeleteDto.getProjectParticipantId(),
                participantDeleteDto.getProjectId())) {
            throw new CannotDeleteException("삭제에 실패했습니다.");
        }
        return true;
        }
}
