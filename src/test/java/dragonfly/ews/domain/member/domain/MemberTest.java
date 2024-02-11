package dragonfly.ews.domain.member.domain;

import dragonfly.ews.domain.member.exception.CannotChangePasswordException;
import dragonfly.ews.domain.member.repository.MemberRepository;
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
class MemberTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EntityManager em;

    @Description("비밀번호는 PasswordEncoder 로 인코딩이 되어야 한다.")
    @Test
    void signUp() {
        String password = "12345";
        Member member = new Member("kkk@gmail.com", 20, MemberRole.ROLE_USER);
        member.changePassword(passwordEncoder, password);
        memberRepository.save(member);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();

        assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
        // encode() 는 솔트 문자열이 추가되므로 matches 문자열을 이용해야한다.
        assertThat(passwordEncoder.matches(password, findMember.getPassword())).isTrue();
    }

    @Description("같은 비밀번호로는 변경할 수 없다.")
    @Test
    void changePassword_fail() {
        String password = "12345";
        Member member = new Member("kkk@gmail.com", 20, MemberRole.ROLE_USER);
        member.changePassword(passwordEncoder, password);
        memberRepository.save(member);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();

        assertThatThrownBy(() -> {
            findMember.changePassword(passwordEncoder, password);
        }).isInstanceOf(CannotChangePasswordException.class);
    }
}