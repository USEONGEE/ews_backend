package dragonfly.ews.domain.project.service;

import dragonfly.ews.domain.project.domain.Project;
import dragonfly.ews.domain.project.exception.NoSuchProjectException;
import dragonfly.ews.domain.project.repository.ParticipateProjectRepository;
import dragonfly.ews.domain.project.repository.ProjectParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipateProjectService {
    private final ParticipateProjectRepository participateProjectRepository;

    public List<Project> findAll(Long memberId) {
        return participateProjectRepository.findProjectsByParticipantId(memberId);
    }

    public Project findOne(Long memberId, Long projectId) {
        return participateProjectRepository.findProjectByParticipantId(memberId, projectId)
                .orElseThrow(NoSuchProjectException::new);
    }
}
