package dragonfly.ews.domain.member.repository;

import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EntityManager em;

    @Test
    void findByEmail_success() {
        Member member = new Member("shdbtjd8@gmail.com", 20, MemberRole.ROLE_USER);
        String password = "12345";
        member.changePassword(passwordEncoder, password);
        memberRepository.save(member);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElse(null);
        assertThat(findMember.getId()).isEqualTo(member.getId());
    }

    @Test
    void findByEmail_fail() {
        Member member = memberRepository.findByEmail("null").orElse(null);
        assertThat(member).isNull();
    }


}