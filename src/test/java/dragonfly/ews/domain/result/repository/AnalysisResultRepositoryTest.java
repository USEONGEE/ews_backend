package dragonfly.ews.domain.result.repository;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class AnalysisResultRepositoryTest {
    @Autowired
    AnalysisResultRepository analysisResultRepository;
    @Autowired
    MemberFileRepository memberFileRepository;
    @Autowired
    MemberFileLogRepository memberFileLogRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EntityManager em;

    @DisplayName("맴버가 소유한 FileResult가 나와야한다.")
    @Test
    void findResultFileByIdAuth_success() {
        Member member = createMember("shdbtjd9@naver.com");
        MemberFile memberFile = createMemberFile(member);
        MemberFileLog memberFileLog = memberFile.getMemberFileLogs().get(0);
        AnalysisResult analysisResult = createAnalysisResult(memberFileLog);

        em.flush();
        em.clear();

        AnalysisResult findResult = analysisResultRepository.findResultFileByIdAuth(member.getId(),
                analysisResult.getId()).orElse(null);
        assertThat(findResult).isNotNull();
    }

    @DisplayName("맴버가 소유하지 않은 fileResult를 찾으면은 파일을 찾을 수 없다.")
    @Test
    void findResultFileByIdAuth_notFount() {
        Member member = createMember("shdbtjd9@naver.com");
        MemberFile memberFile = createMemberFile(member);
        MemberFileLog memberFileLog = memberFile.getMemberFileLogs().get(0);
        AnalysisResult analysisResult = createAnalysisResult(memberFileLog);

        Member anotherMember = createMember("ss@naver.com");
        memberRepository.save(anotherMember);

        em.flush();
        em.clear();

        AnalysisResult findResult = analysisResultRepository.findResultFileByIdAuth(anotherMember.getId(),
                analysisResult.getId()).orElse(null);

        assertThat(findResult).isNull();
    }

    @DisplayName("MemberFileLog 가 가진 result 들을 전부 찾는다.")
    @Test
    void findResultFileByFileLogId_success() {
        Member member = createMember("shdbtjd9@naver.com");
        MemberFile memberFile = createMemberFile(member);
        MemberFileLog memberFileLog = memberFile.getMemberFileLogs().get(0);
        AnalysisResult result1 = createAnalysisResult(memberFileLog);
        AnalysisResult result2 = createAnalysisResult(memberFileLog);

        em.flush();
        em.clear();

        AnalysisResult findResult1 = analysisResultRepository.findById(result1.getId())
                .orElse(null);
        AnalysisResult findResult2 = analysisResultRepository.findById(result2.getId())
                .orElse(null);
        MemberFileLog findMemberFileLog = memberFileLogRepository.findById(memberFileLog.getId())
                .orElse(null);
        List<AnalysisResult> resultFileByFileLogId = analysisResultRepository
                .findResultFileByFileLogId(findMemberFileLog.getId());

        assertThat(resultFileByFileLogId)
                .containsExactlyInAnyOrder(findResult1, findResult2);
    }

    Member createMember(String mail) {
        Member member = new Member(mail, 20, MemberRole.ROLE_USER);
        member.changePassword(passwordEncoder, "12345");
        return memberRepository.save(member);
    }

    MemberFile createMemberFile(Member member) {
        MemberFile memberFile = new MemberFile(member, "file.txt", "saved.txt");
        return memberFileRepository.save(memberFile);
    }

    AnalysisResult createAnalysisResult(MemberFileLog memberFileLog) {
        AnalysisResult analysisResult = new AnalysisResult(memberFileLog, AnalysisStatus.PROCESSING);
        return analysisResultRepository.save(analysisResult);
    }

}