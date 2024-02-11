package dragonfly.ews.domain.result.domain;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.project.repository.ProjectRepository;
import dragonfly.ews.domain.result.repository.FileAnalysisResultRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class FileAnalysisResultTest {
    @Autowired
    FileAnalysisResultRepository fileAnalysisResultRepository;
    @Autowired
    MemberFileLogRepository memberFileLogRepository;
    @Autowired
    MemberFileRepository memberFileRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    EntityManager em;

    @Test
    void create() {
        Member member = createMember();
        MemberFile memberFile = createMemberFile(member);
        FileAnalysisResult fileAnalysisResult = new FileAnalysisResult(memberFile.getMemberFileLogs().get(0), AnalysisStatus.PROCESSING);
        fileAnalysisResultRepository.save(fileAnalysisResult);

        em.flush();
        em.clear();

        FileAnalysisResult findResult = fileAnalysisResultRepository.findById(fileAnalysisResult.getId()).get();
        MemberFile findMemberFile = memberFileRepository.findById(memberFile.getId()).get();
        Member findMember = memberRepository.findById(member.getId()).get();

        assertThat(findResult.getMemberFileLog()).isEqualTo(findMemberFile.getMemberFileLogs().get(0));
        assertThat(findResult.getAnalysisStatus()).isEqualTo(AnalysisStatus.PROCESSING);
        assertThat(findResult.getMemberFileLog().getMemberFile().getOwner()).isEqualTo(findMember);
    }

    Member createMember() {
        Member newMember = new Member("shdbtjd9@naver.com", 20, MemberRole.ROLE_USER);
        newMember.changePassword(passwordEncoder, "12345");
        return memberRepository.save(newMember);
    }

    MemberFile createMemberFile(Member member) {
        MemberFile memberFile = new MemberFile(member, "file.txt", "savedFile.txt");
        return memberFileRepository.save(memberFile);
    }

}