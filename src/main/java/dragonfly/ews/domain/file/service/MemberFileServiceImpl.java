package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.MemberFileUtils;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.exception.AccessDeniedException;
import dragonfly.ews.domain.file.exception.CannotSaveFileException;
import dragonfly.ews.domain.file.exception.NoFileNameException;
import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.exception.NoSuchMemberException;
import dragonfly.ews.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberFileServiceImpl implements MemberFileService {

    @Value("${file.dir}")
    private String fileDir;

    private final MemberFileRepository memberFileRepository;
    private final MemberRepository memberRepository;
    private final MemberFileUtils memberFileUtils;

    /**
     *
     */
    @Transactional
    @Override
    public void saveFile(MultipartFile file, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));

        // 파일 이름 가져오고 저장할 파일이름 만들기
        String originalFilename = file.getOriginalFilename();
        hasName(originalFilename);
        String savedFilename = memberFileUtils.createSavedFilename(originalFilename);

        // 파일 데이터, 파일 메타데이터 저장
        MemberFile memberFile = new MemberFile(member, originalFilename, savedFilename);
        memberFileRepository.save(memberFile);
        storeFile(file, savedFilename);
    }

    @Transactional
    @Override
    public void updateFile(MultipartFile file, Long memberId, Long fileId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));
        MemberFile memberFile = memberFileRepository.findById(fileId)
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));

        validationFileOwner(member, memberFile);
        String savedFilename = memberFileUtils.createSavedFilename(memberFile.getFileName());

        // 파일 데이터, 파일 메타데이터 저장
        memberFile.updateFile(savedFilename);
        storeFile(file, savedFilename);
    }

    @Override
    public MemberFile findMemberFileById(Long memberId, Long fileId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));
        MemberFile memberFile = memberFileRepository.findById(fileId)
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));

        validationFileOwner(member, memberFile);

        return null;
    }

    @Override
    public List<MemberFile> findAll(Long memberId) {
        return null;
    }

    /**
     * 클라이언트가 제공한 파일의 이름이 존재하는 지를 검증
     * @param fileName
     * @exception NoFileNameException
     */
    private void hasName(String fileName) {
        if (fileName.isEmpty()) {
            throw new NoFileNameException("사용자가 제공한 파일에 이름이 없습니다.");
        }
    }

    /**
     * 파일을 실제 물리적으로 저장하는 로직
     * @param file
     * @param savedFilename
     * @exception  CannotSaveFileException
     */
    private void storeFile(MultipartFile file, String savedFilename) {
        try {
            file.transferTo(new File(fileDir + savedFilename));
        } catch (IOException e) {
            log.error("파일 저장에 문제가 발생했습니다.");
            throw new CannotSaveFileException(e);
        }
    }

    /**
     * 파일의 주인이 맞는지에 대한 검증
     * @param member
     * @param memberFile
     * @exception AccessDeniedException
     */
    private void validationFileOwner(Member member, MemberFile memberFile) {
        if (!memberFile.getOwner().equals(member)) {
            throw new AccessDeniedException("해당 회원은 파일에 대한 권한이 없습니다.");
        }
    }
}
