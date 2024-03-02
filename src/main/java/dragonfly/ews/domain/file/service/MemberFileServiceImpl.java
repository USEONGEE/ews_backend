package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileUpdateDto;
import dragonfly.ews.domain.file.exception.*;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.dto.MemberFileLogCreateDto;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.exception.NoSuchMemberException;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.project.domain.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberFileServiceImpl implements MemberFileService {

    private final MemberFileRepository memberFileRepository;
    private final MemberRepository memberRepository;
    private final MemberFileLogRepository memberFileLogRepository;
    private final FileUtils memberFileUtils;

    /**
     * [파일 저장]
     *
     * @param file
     * @param memberId
     */

    @Deprecated
    @Transactional
    @Override
    public boolean saveFile(MultipartFile file, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));

        // 파일 이름 가져오고 저장할 파일이름 만들기
        String originalFilename = file.getOriginalFilename();
        hasName(originalFilename);
        String savedFilename = memberFileUtils.createSavedFilename(originalFilename);

        // 파일 데이터, 파일 메타데이터 저장
        MemberFile memberFile = new MemberFile(member, originalFilename, savedFilename);
        memberFileRepository.save(memberFile);
        memberFileUtils.storeFile(file, savedFilename);

        return true;
    }

    @Transactional
    @Override
    public boolean saveFile(Long memberId, MemberFileCreateDto memberFileCreateDto) {
        MultipartFile file = memberFileCreateDto.getFile();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));

        // 파일 이름 가져오고 저장할 파일이름 만들기
        String originalFilename = memberFileCreateDto.getFileName();
        hasName(originalFilename);
        String savedFilename = memberFileUtils.createSavedFilename(originalFilename);

        // MemberFile 저장하기
        MemberFile memberFile = new MemberFile(member, originalFilename, savedFilename);
        memberFile.changeDescription(memberFileCreateDto.getDescription());
        memberFileRepository.save(memberFile);
        
        // 파일 저장하기
        memberFileUtils.storeFile(file, savedFilename);


        return true;
    }


    @Override
    @Transactional
    public boolean updateFile(Long memberId, MemberFileUpdateDto memberFileUpdateDto) {
        MemberFile memberFile = memberFileRepository.findByIdAuth(memberId, memberFileUpdateDto.getMemberFileId())
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));
        // 검증
        hasProject(memberFile);
        // 저장될 파일명 생성
        String savedFilename = memberFileUtils.createSavedFilename(memberFile.getFileName());
        // MemberFileLog 생성 및 저장
        MemberFileLogCreateDto memberFileLogCreateDto =
                MemberFileLogCreateDto.of(savedFilename, memberFileUpdateDto.getDescription());
        memberFile.updateFile(memberFileLogCreateDto);
        // 파일 저장
        memberFileUtils.storeFile(memberFileUpdateDto.getFile(), savedFilename);

        return true;
    }

    /**
     * [맴버가 가지고 있는 파일 업데이트]
     *
     * @param file
     * @param memberId
     * @param fileId
     */
    @Deprecated
    @Transactional
    @Override
    public boolean updateFile(MultipartFile file, Long memberId, Long fileId) {
        MemberFile memberFile = memberFileRepository.findByIdAuth(memberId, fileId)
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));

        hasProject(memberFile);

        String savedFilename = memberFileUtils.createSavedFilename(memberFile.getFileName());

        // 파일 데이터, 파일 메타데이터 저장
        memberFile.updateFile(savedFilename);
        memberFileUtils.storeFile(file, savedFilename);

        return true;
    }

    @Override
    public MemberFile findByIdContainLogs(Long memberId, Long memberFileId) {
        memberFileRepository.findByIdAuth(memberId, memberFileId)
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));
        return memberFileRepository.findByIdContainLogs(memberFileId)
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));
    }

    @Override
    public List<MemberFile> findAll(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));
        List<MemberFile> result = memberFileRepository.findByMemberId(member.getId());

        return result;
    }


    private void hasProject(MemberFile memberFile) {
        Project project = memberFile.getProject();
        if (project == null) {
            throw new FileNotInProjectException("파일이 프로젝트에 포함되지 않았습니다.");
        }
    }

    /**
     * 클라이언트가 제공한 파일의 이름이 존재하는 지를 검증
     *
     * @param fileName
     * @throws NoFileNameException
     */
    private void hasName(String fileName) {
        if (fileName.isEmpty()) {
            throw new NoFileNameException("사용자가 제공한 파일에 이름이 없습니다.");
        }
    }

    /**
     * 파일의 주인이 맞는지에 대한 검증
     *
     * @param member
     * @param memberFile
     * @throws AccessDeniedException
     */
    @Deprecated
    private void validationFileOwner(Member member, MemberFile memberFile) {
        if (!memberFile.getOwner().equals(member)) {
            throw new AccessDeniedException("해당 회원은 파일에 대한 권한이 없습니다.");
        }
    }
}
