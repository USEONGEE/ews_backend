package dragonfly.ews.domain.project.service;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.exception.NoSuchMemberException;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.project.controlelr.ParticipantDto;
import dragonfly.ews.domain.project.controlelr.ProjectCreateDto;
import dragonfly.ews.domain.project.domain.Project;
import dragonfly.ews.domain.project.exception.NoSuchProjectException;
import dragonfly.ews.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final MemberFileRepository memberFileRepository;

    @Override
    public Project createProject(Long ownerId, ProjectCreateDto projectCreateDto) {
        Member member = memberRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchMemberException("회원을 찾을 수 없습니다."));
        Project project = new Project(projectCreateDto.getProjectName(), member);

        projectRepository.save(project);
        return project;
    }

    @Override
    public Project addParticipants(Long ownerId, Long projectId, ParticipantDto... participantDtos) {
        Project project = projectRepository.findByIdAuth(ownerId, projectId)
                .orElseThrow(() -> new NoSuchProjectException("프로젝트를 찾을 수 없습니다."));

        // TODO 1:N 해결하기 -> 한방쿼리 작성
        for (ParticipantDto participantDto : participantDtos) {
            Member participant = memberRepository.findById(participantDto.getParticipantId())
                    .orElseThrow(() -> new NoSuchMemberException("회원을 찾을 수 없습니다."));
            project.addParticipants(participant);
        }
        return project;
    }

    @Override
    public Project addMemberFile(Long ownerId, Long projectId, Long... memberFileIds) {
        Project project = projectRepository.findByIdAuth(ownerId, projectId)
                .orElseThrow(() -> new NoSuchProjectException("프로젝트를 찾을 수 없습니다."));
        for (Long memberFileId : memberFileIds) {
            MemberFile memberFile = memberFileRepository.findByIdAuth(ownerId, memberFileId)
                    .orElseThrow(() -> new NoSuchFileException("파일을 찾을 수 없습니다."));
            project.addMemberFile(memberFile);
        }

        return project;
    }

}