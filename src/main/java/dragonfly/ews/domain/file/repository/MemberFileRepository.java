package dragonfly.ews.domain.file.repository;

import dragonfly.ews.domain.file.domain.MemberFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberFileRepository extends JpaRepository<MemberFile, Long> {

    @Query("SELECT mf FROM MemberFile mf WHERE mf.id = :fileId AND " +
            "(mf.owner.id = :memberId OR " +
            "mf.project.id IN (SELECT pp.project.id FROM ParticipateProject pp WHERE pp.member.id = :memberId))")
    Optional<MemberFile> findByIdAuth(@Param("memberId") Long memberId,
                                      @Param("fileId") Long memberFileId);

    @Query("select mf from MemberFile mf join fetch mf.memberFileLogs where mf.id=:id")
    Optional<MemberFile> findByIdContainLogs(@Param("id") Long memberFileId);

    @Query("select mf from MemberFile mf where mf.owner.id = :id")
    List<MemberFile> findByMemberId(@Param("id") Long memberId);

    @Query("select mf from MemberFile mf where mf.owner.id = :ownerId and mf.id = :fileId")
    Optional<MemberFile> findByIdAndOwnerId(@Param("ownerId") Long ownerId,
                                            @Param("fileId") Long memberFileId);
}
