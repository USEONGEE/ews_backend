package dragonfly.ews.domain.filelog.repository;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberFileLogRepository extends JpaRepository<MemberFileLog, Long> {
    @Query("select mfl from MemberFileLog mfl where mfl.id = :memberFileLogId and mfl.memberFile.owner.id = :memberId")
    Optional<MemberFileLog> findMemberFileLogByIdAuth(@Param("memberId") Long memberId,
                                                      @Param("memberFileLogId") Long memberFileLogId);
}
