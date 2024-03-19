package dragonfly.ews.domain.filelog.repository;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberFileLogRepository extends JpaRepository<MemberFileLog, Long> {
    @Query("SELECT mfl FROM MemberFileLog mfl join fetch mfl.memberFile WHERE mfl.id = :memberFileLogId AND " +
            "(mfl.memberFile.owner.id = :memberId OR " +
            "mfl.memberFile.project.id IN " +
            "(SELECT pp.project.id FROM ParticipateProject pp WHERE pp.member.id = :memberId))")
//    @Query("select mfl from MemberFileLog mfl where mfl.id = :memberFileLogId and mfl.memberFile.owner.id = :memberId")
    Optional<MemberFileLog> findByIdAuth(@Param("memberId") Long memberId,
                                         @Param("memberFileLogId") Long memberFileLogId);

    @Query("select mfl from MemberFileLog mfl left join fetch mfl.analysisResults far where mfl.id = :memberFileLogId")
    Optional<MemberFileLog> findByIdContainResult(@Param("memberFileLogId") Long memberFileLogId);

    @Query(value = "select mfl from MemberFileLog mfl left join fetch mfl.memberFile where mfl.memberFile.owner.id = :memberId",
            countQuery = "select count(mfl) from MemberFileLog mfl where mfl.memberFile.owner.id = :memberId")
    Page<MemberFileLog> findByOwnerId(@Param("memberId") Long memberId, Pageable pageable);

    List<MemberFileLog> findByMemberFileId(Long memberFileId);
}
