package dragonfly.ews.domain.member.repository;

import dragonfly.ews.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByRefreshToken(String refreshToken);

    List<Member> findByIdIn(List<Long> ids);

    List<Member> findByEmailContaining(String emailFragment);

}
