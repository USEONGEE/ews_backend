package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.exception.*;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
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
    @Transactional
    @Override
    public MemberFile saveFile(MultipartFile file, Long memberId) {
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
        return memberFile;
    }

    /**
     * [맴버가 가지고 있는 파일 업데이트]
     *
     * @param file
     * @param memberId
     * @param fileId
     */
    @Transactional
    @Override
    public void updateFile(MultipartFile file, Long memberId, Long fileId) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));
//        MemberFile memberFile = memberFileRepository.findById(fileId)
//                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));
//        validationFileOwner(member, memberFile);
        MemberFile memberFile = memberFileRepository.findByIdAuth(memberId, fileId)
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));

        hasProject(memberFile);

        String savedFilename = memberFileUtils.createSavedFilename(memberFile.getFileName());

        // 파일 데이터, 파일 메타데이터 저장
        memberFile.updateFile(savedFilename);
        memberFileUtils.storeFile(file, savedFilename);
    }

    @Override
    public List<MemberFileLog> findMemberFileDetails(Long memberId, Long memberFileId) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));
//        MemberFile memberFile = memberFileRepository.findMemberFileByIdWithLogs(fileId)
//                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));
//        // TODO validation을 해야하는지 생각해보기
//        validationFileOwner(member, memberFile);

        MemberFile memberFile = memberFileRepository.findByIdAuth(memberId, memberFileId)
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));

        return memberFile.getMemberFileLogs();
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
