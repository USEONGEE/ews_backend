package dragonfly.ews.domain.file.domain;

import dragonfly.ews.domain.file.exception.ExtensionNotFoundException;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.project.domain.Project;
import dragonfly.ews.domain.project.repository.ProjectRepository;
import jakarta.persistence.EntityManager;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberFileTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberFileRepository memberFileRepository;
    @Autowired
    MemberFileLogRepository memberFileLogRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    EntityManager em;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void create() {
        Member member = createMember();
        String originalFilename = "file.txt";
        String savedFilename = "saved.txt";
        String fileType = "txt";

        MemberFile memberFile = new MemberFile(member, originalFilename, savedFilename);
        memberFileRepository.save(memberFile);
        Project project = createProject(member);
        memberFile.addProject(project);

        em.flush();
        em.clear();

        MemberFile findMemberFile = memberFileRepository.findById(memberFile.getId()).get();
        Member findMember = memberRepository.findById(member.getId()).get();
        Project findProject = projectRepository.findById(project.getId()).get();

        assertThat(findMemberFile.getFileName()).isEqualTo(originalFilename);
        assertThat(findMemberFile.getFileType()).isEqualTo(fileType);
        assertThat(findMemberFile.getOwner()).isEqualTo(findMember);
        assertThat(findMemberFile.getCreatedDate()).isNotNull();
        assertThat(findMemberFile.getMemberFileLogs()).isNotNull();
        assertThat(findMemberFile.getProject()).isEqualTo(findProject);
        assertThat(findMemberFile.getMemberFileLogs().get(0).getSavedName()).isEqualTo(savedFilename);
    }

    @Description("확장자가 존재하지 않는 파일은 저장될 수 없다.")
    @Test
    void extension_fail() {
        Member member = createMember();

        assertThatThrownBy(() -> {
            MemberFile memberFile = new MemberFile(member, "file", "file.txt");
        }).isInstanceOf(ExtensionNotFoundException.class);
    }

    @Description("OS에 저장되는 파일명도 확장자가 존재해야한다.")
    @Test
    void savedExtension_fail() {
        Member member = createMember();

        assertThatThrownBy(() -> {
            MemberFile memberFile = new MemberFile(member, "file.txt", "file");
        }).isInstanceOf(ExtensionNotFoundException.class);
    }

    @Description("최초 파일을 저장하면 MemberFileLog 객체도 저장되어야 한다.")
    @Test
    void shouldMemberFileLogCreated() {
        Member member = createMember();
        String savedFilename = "saved.txt";
        MemberFile memberFile = new MemberFile(member, "file.txt", savedFilename);
        memberFileRepository.save(memberFile);
        em.flush();
        em.clear();

        MemberFile findMemberFile = memberFileRepository.findById(memberFile.getId()).get();

        assertThat(findMemberFile.getMemberFileLogs().size()).isEqualTo(1);
        assertThat(findMemberFile.getMemberFileLogs().get(0).getSavedName()).isEqualTo(savedFilename);
    }

    @Description("파일을 삭제하면 파일 로그 삭제되어야 한다.")
    @Test
    void fileLogShouldBeDeletedWhenFileBeDeleted() {
        Member member = createMember();
        MemberFile memberFile = new MemberFile(member, "file.txt", "savedFile.txt");
        memberFileRepository.save(memberFile);

        MemberFile findMemberFile = memberFileRepository.findById(memberFile.getId()).get();
        MemberFileLog memberFileLog = findMemberFile.getMemberFileLogs().get(0);
        memberFileRepository.delete(findMemberFile);
        em.flush();
        em.clear();
        MemberFileLog findMemberFileLog = memberFileLogRepository.findById(memberFileLog.getId())
                .orElse(null);

        assertThat(findMemberFileLog).isNull();
    }

    Member createMember() {
        Member newMember = new Member("shdbtjd9@naver.com", 20, MemberRole.ROLE_USER);
        newMember.changePassword(passwordEncoder, "12345");
        return memberRepository.save(newMember);
    }

    Project createProject(Member member) {
        Project project = new Project("project", member);
        return projectRepository.save(project);
    }
}