package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.aop.annotation.HasMultipartFile;
import dragonfly.ews.domain.file.aop.annotation.UseMemberFileManager;
import dragonfly.ews.domain.file.aop.utils.MemberFileManger;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final MemberFileManger memberFileManger;

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
        MemberFile memberFile = memberFileManger.createMemberFile(member, memberFileCreateDto);
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
        memberFileManger.beforeUpdateValidate(memberFile, memberFileUpdateDto.getFile());

        // 저장될 파일명 생성
        String savedFilename = memberFileUtils.createSavedFilename(memberFile.getOriginalName());
        // MemberFileLog 생성 및 저장
        MemberFileLogCreateDto memberFileLogCreateDto =
                MemberFileLogCreateDto.of(savedFilename, memberFileUpdateDto.getDescription());
        memberFile.updateFile(memberFileLogCreateDto);
        // 파일 저장
        memberFileUtils.storeFile(memberFileUpdateDto.getFile(), savedFilename);

        return true;
    }

    public MemberFile findByIdContainLogs(Long memberId, Long memberFileId) {
        memberFileRepository.findByIdAuth(memberId, memberFileId)
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));
        return memberFileRepository.findByIdContainLogs(memberFileId)
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));
    }

    public List<MemberFile> findAll(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."))  ;
        List<MemberFile> result = memberFileRepository.findByMemberId(member.getId());

        return result;
    }

}
