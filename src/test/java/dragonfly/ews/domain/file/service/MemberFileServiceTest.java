package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.exception.*;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.project.dto.ProjectCreateDto;
import dragonfly.ews.domain.project.domain.Project;
import dragonfly.ews.domain.project.service.ProjectService;
import jakarta.persistence.EntityManager;
import jdk.jfr.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class MemberFileServiceTest {

    @Autowired
    ProjectService projectService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberFileService memberFileService;
    @Autowired
    FileUtils fileUtils;
    @Mock
    MultipartFile multipartFile;
    @Autowired
    EntityManager em;


    @Test
    void saveFile_success() {
        when(multipartFile.getOriginalFilename()).thenReturn("file.txt");
        Member member = createMember("s@gmail.com");
        memberFileService.saveFile(multipartFile, member.getId());
        MemberFile memberFile = member.getMemberFiles().get(0);

        MemberFileLog memberFileLog = memberFile.getMemberFileLogs().get(0);

        assertThat(memberFile).isNotNull();
        assertThat(memberFileLog).isNotNull();
    }

    @Description("파일 이름이 없으면 저장에 실패한다.")
    @Test
    void saveFile_fail_noFileName() {
        when(multipartFile.getOriginalFilename()).thenReturn("");
        Member member = createMember("s@gmail.com");

        assertThatThrownBy(() -> memberFileService.saveFile(multipartFile, member.getId()))
                .isInstanceOf(NoFileNameException.class);
    }

    @Description("파일 확장자가 없으면 저장에 실패한다.")
    @Test
    void saveFile_fail_noExtension() {
        when(multipartFile.getOriginalFilename()).thenReturn("file");
        Member member = createMember("s@gmail.com");

        assertThatThrownBy(() -> memberFileService.saveFile(multipartFile, member.getId()))
                .isInstanceOf(ExtensionNotFoundException.class);
    }

    @Description("파일 저장에서 예외가 발생한 경우에는 saveFile() 전체가 취소된다. -> Runtime예외 발생")
    @Test
    void saveFile_fila_IOException() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("file.txt"); // 리턴값이 있는 경우 모킹
        doThrow(new IOException()).when(multipartFile).transferTo((File) any()); // 리턴값이 없는 경우 모킹
        Member member = createMember("s@gmail.com");

        assertThatThrownBy(() -> memberFileService.saveFile(multipartFile, member.getId()))
                .isInstanceOf(CannotSaveFileException.class);
    }

    @Test
    void updateFile_success() {
        when(multipartFile.getOriginalFilename()).thenReturn("file.txt");
        Member member = createMember("s@gmail.com");
        memberFileService.saveFile(multipartFile, member.getId());
        MemberFile memberFile = member.getMemberFiles().get(0);
        addMemberFileToProject(member, memberFile);

        memberFileService.updateFile(multipartFile, member.getId(), memberFile.getId());

        assertThat(memberFile.getMemberFileLogs().size()).isEqualTo(2);
    }

    @DisplayName("파일이 프로젝트에 포함되지 않았으면 파일을 업데이트할 수 없다.")
    @Test
    void updateFile_fail_noProject() {
        when(multipartFile.getOriginalFilename()).thenReturn("file.txt");
        Member member = createMember("s@gmail.com");

        memberFileService.saveFile(multipartFile, member.getId());
        MemberFile memberFile = member.getMemberFiles().get(0);

        assertThatThrownBy(() -> memberFileService.updateFile(multipartFile, member.getId(), memberFile.getId()))
                .isInstanceOf(FileNotInProjectException.class);
    }

    @DisplayName("자신의 파일이 아닌 것을 추가하려고 하면 에외 발생")
    @Test
    void updateFile_fail_noAuth() {
        when(multipartFile.getOriginalFilename()).thenReturn("file.txt");
        Member member = createMember("s@gmail.com");
        Member anotherMember = createMember("ss@gmail.com");

        memberFileService.saveFile(multipartFile, member.getId());
        MemberFile memberFile = member.getMemberFiles().get(0);
        addMemberFileToProject(member, memberFile);
        memberFileService.saveFile(multipartFile, anotherMember.getId());

        MemberFile anotherFile = anotherMember.getMemberFiles().get(0);

        assertThatThrownBy(() -> memberFileService.updateFile(multipartFile, member.getId(), anotherFile.getId()))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    void findMemberFileDetails_success() {
        when(multipartFile.getOriginalFilename())
                .thenReturn("file.txt")
                .thenReturn("file2.txt");
        Member member = createMember("s@gmail.com");
        memberFileService.saveFile(multipartFile, member.getId());
        MemberFile memberFile = member.getMemberFiles().get(0);
        addMemberFileToProject(member, memberFile);
        memberFileService.updateFile(multipartFile, member.getId(), memberFile.getId());

        MemberFile findMemberFile = memberFileService.findByIdContainLogs(member.getId(), memberFile.getId());

        assertThat(findMemberFile.getMemberFileLogs().size()).isEqualTo(2);
    }

    @DisplayName("자신의 파일이 아닌 것을 조회하려고하면 예외발생")
    @Test
    void findMemberFileDetails_fail_noAuth() {
        when(multipartFile.getOriginalFilename())
                .thenReturn("file.txt")
                .thenReturn("file2.txt");
        Member member = createMember("s@gmail.com");
        memberFileService.saveFile(multipartFile, member.getId());
        MemberFile memberFile = member.getMemberFiles().get(0);
        addMemberFileToProject(member, memberFile);
        memberFileService.updateFile(multipartFile, member.getId(), memberFile.getId());

        Member anotherMember = createMember("ss@gmail.com");
        assertThatThrownBy(() -> memberFileService.findByIdContainLogs(anotherMember.getId(), memberFile.getId()))
                .isInstanceOf(NoSuchFileException.class);
    }

    void addMemberFileToProject(Member member, MemberFile memberFile) {
        ProjectCreateDto projectCreateDto = new ProjectCreateDto("project", null);
        Project project = projectService.createProject(member.getId(), projectCreateDto);
        projectService.addMemberFile(member.getId(), project.getId(), memberFile.getId());
    }

    Member createMember(String mail) {
        Member member = new Member(mail, 20, MemberRole.ROLE_USER);
        member.changePassword(passwordEncoder, "12345");
        return memberRepository.save(member);
    }

    Project createProject(Long ownerId) {
        ProjectCreateDto projectCreateDto = new ProjectCreateDto("project", null);
        return projectService.createProject(ownerId, projectCreateDto);
    }

    @Test
    void test() {
        System.out.println("memberFileService = " + memberFileService.getClass());
    }

}