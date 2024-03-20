package dragonfly.ews.domain.member.repository;

import dragonfly.ews.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepositoryApi extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m " +
            "WHERE m.email LIKE %:email% " +
            "AND NOT EXISTS ( " +
            "SELECT mp FROM ProjectParticipant mp WHERE mp.member = m AND mp.project.id = :projectId) " +
            "AND NOT EXISTS ( " +
            "SELECT p FROM Project p WHERE p.owner = m AND p.id = :projectId)")
    List<Member> findNewParticipants(@Param("email") String email, @Param("projectId") Long projectId);
}

