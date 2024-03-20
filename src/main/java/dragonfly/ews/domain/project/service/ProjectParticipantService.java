package dragonfly.ews.domain.project.service;

import dragonfly.ews.domain.project.domain.ProjectParticipant;
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

    public List<ProjectParticipant> findParticipates(Long memberId, Long projectId) {
        projectRepository.findByIdAuth(memberId, projectId)
                .orElseThrow(NoSuchProjectException::new);
        return projectParticipantRepository.findByProjectIdContainMember(projectId);
    }
}
