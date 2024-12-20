package dragonfly.ews.domain.project.service;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.exception.NoSuchMemberException;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.project.dto.ParticipantDto;
import dragonfly.ews.domain.project.dto.ProjectCreateDto;
import dragonfly.ews.domain.project.domain.Project;
import dragonfly.ews.domain.project.exception.NoSuchProjectException;
import dragonfly.ews.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final MemberFileRepository memberFileRepository;

    /**
     * [프로젝트 생성]
     *
     * @param ownerId
     * @param projectCreateDto
     * @return
     */
    @Transactional
    public Project createProject(Long ownerId, ProjectCreateDto projectCreateDto) {
        Member member = memberRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchMemberException("회원을 찾을 수 없습니다."));
        Project project = new Project(projectCreateDto.getProjectName(), member);
        project.changeDescription(projectCreateDto.getDescription());

        projectRepository.save(project);
        return project;
    }

    /**
     * [프로젝트 참여자 생성]
     *
     * @param ownerId
     * @param projectId
     * @param participantDtos
     * @return
     */
    @Transactional
    public Project addParticipants(Long ownerId, Long projectId, ParticipantDto... participantDtos) {
        Project project = projectRepository.findByIdAuth(ownerId, projectId)
                .orElseThrow(() -> new NoSuchProjectException("프로젝트를 찾을 수 없습니다."));

        List<Long> collect = Arrays.stream(participantDtos)
                .sequential()
                .map(dto -> dto.getParticipantId())
                .collect(Collectors.toList());

        List<Member> members = memberRepository.findByIdIn(collect);
        for (Member member : members) {
            project.addParticipants(member);
        }

        return project;
    }

    /**
     * [프로젝트에 참여시킬 파일 추가]
     *
     * @param ownerId
     * @param projectId
     * @param memberFileIds
     * @return
     */
    @Transactional
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

    public List<Project> findAll(Long memberId) {
        return projectRepository.findAll(memberId);
    }

    public Project findOne(Long memberId, Long projectId) {
        return projectRepository.findByIdAndOwnerId(projectId, memberId)
                .orElseThrow(NoSuchProjectException::new);
    }

    @Transactional
    public boolean deleteOne(Long memberId, Long projectId) {
//        projectRepository.deleteByOwner(memberId, projectId); // cascade가 동작 안 함
        projectRepository.findByIdAuth(memberId, projectId)
                .orElseThrow(NoSuchProjectException::new);
        projectRepository.deleteById(projectId);

        return true;
    }
}
