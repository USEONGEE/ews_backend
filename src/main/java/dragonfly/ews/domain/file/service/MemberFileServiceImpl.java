package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileUpdateDto;
import dragonfly.ews.domain.file.exception.*;
import dragonfly.ews.domain.file.factory.MemberFileFactory;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberFileServiceImpl implements MemberFileService {

    private final MemberFileRepository memberFileRepository;
    private final MemberRepository memberRepository;
    private final FileUtils memberFileUtils;
    private final ProjectRepository projectRepository;
    private final MemberFileFactory memberFileFactory;


    @Transactional
    @Override
    public boolean saveFile(Long memberId, MemberFileCreateDto memberFileCreateDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 존재하지 않습니다."));
        Project project = projectRepository.findById(memberFileCreateDto.getProjectId())
                .orElseThrow(NoSuchProjectException::new);

        // 파일 이름 가져오고 저장할 파일이름 만들기
        // MemberFile 저장하기
        MemberFile memberFile = memberFileFactory.create(member, memberFileCreateDto);
        memberFile.changeProject(project);
        memberFile.changeDescription(memberFileCreateDto.getDescription());
        memberFileRepository.save(memberFile);

        return true;
    }


    @Override
    @Transactional
    public boolean updateFile(Long memberId, MemberFileUpdateDto memberFileUpdateDto) {
        MemberFile memberFile = memberFileRepository.findByIdAuth(memberId, memberFileUpdateDto.getMemberFileId())
                .orElseThrow(() -> new NoSuchFileException("해당 파일을 찾을 수 없습니다."));
        // 검증
        // 프로젝트에 포함되어 있는지
        hasProject(memberFile);
        // 파일 확장자가 같은지
        String fileExt = memberFileUtils.getFileExt(memberFileUpdateDto.getFile().getOriginalFilename());
        checkFileExtension(memberFile, fileExt);
        // TODO 파일 column이 같은지 + 데이터 타입이 같은지

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
        checkFileExtension(memberFile, file.getOriginalFilename());

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


    private void checkFileExtension(MemberFile memberFile, String originalFilename) {
        if (!memberFile.isSameExt(originalFilename)) {
            throw new ExtensionMismatchException("파일 확장자가 같아야합니다.");
        }
    }
}
