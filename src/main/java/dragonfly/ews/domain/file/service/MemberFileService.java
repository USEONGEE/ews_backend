package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.file.aop.annotation.HasMultipartFile;
import dragonfly.ews.domain.file.aop.annotation.UseMemberFileManager;
import dragonfly.ews.domain.file.aop.utils.MemberFileManager;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileUpdateDto;
import dragonfly.ews.domain.file.exception.*;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.dto.MemberFileLogCreateDto;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.exception.NoSuchMemberException;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.project.domain.Project;
import dragonfly.ews.domain.project.exception.NoSuchProjectException;
import dragonfly.ews.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberFileService {

    private final MemberFileRepository memberFileRepository;
    private final MemberRepository memberRepository;
    private final FileUtils memberFileUtils;
    private final ProjectRepository projectRepository;
    // UseMemberFileManager 가 붙어야 사용 가능
    private final MemberFileManager memberFileManager;


    @UseMemberFileManager
    @HasMultipartFile
    @Transactional
    public boolean saveFile(Long memberId, MemberFileCreateDto memberFileCreateDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));
        Project project = projectRepository.findById(memberFileCreateDto.getProjectId())
                .orElseThrow(NoSuchProjectException::new);

        // 파일 이름 가져오고 저장할 파일이름 만들기
        // MemberFile 저장하기
        MemberFile memberFile = memberFileManager.createMemberFile(member, memberFileCreateDto);
        memberFile.changeProject(project);
        memberFile.changeDescription(memberFileCreateDto.getDescription());
        memberFileRepository.save(memberFile);

        // 파일 저장 이후 후속 조치

        return true;
    }

    @UseMemberFileManager
    @HasMultipartFile
    @Transactional
    public boolean updateFile(Long memberId, MemberFileUpdateDto memberFileUpdateDto) {
        MemberFile memberFile = memberFileRepository.findByIdAuth(memberId, memberFileUpdateDto.getMemberFileId())
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));

        // 검증
        memberFileManager.beforeUpdateValidate(memberFile, memberFileUpdateDto.getFile());

        // 업데이트
        memberFileManager.update(memberFile, memberFileUpdateDto);
        return true;
    }

    @UseMemberFileManager
    public Object findByIdContainLogs(Long memberId, Long memberFileId) {
        return memberFileManager.findDtoById(memberId, memberFileId);
    }

    public Page<MemberFile> findPaging(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));
        Page<MemberFile> result = memberFileRepository.findByMemberId(member.getId(), pageable);

        return result;
    }

}
