package dragonfly.ews.domain.result.repository;

import dragonfly.ews.domain.result.domain.FileAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileAnalysisResultRepository extends JpaRepository<FileAnalysisResult, Long> {

    @Query("SELECT far FROM FileAnalysisResult far WHERE far.id = :fileAnalysisId AND " +
            "(far.memberFileLog.memberFile.owner.id = :memberId OR " +
            "far.memberFileLog.memberFile.project.id IN " +
            "(SELECT pp.project.id FROM ParticipateProject pp WHERE pp.member.id = :memberId))")
//    @Query("select far from FileAnalysisResult far where far.id = :fileAnalysisId and " +
//            "far.memberFileLog.memberFile.owner.id = :memberId")
    Optional<FileAnalysisResult> findResultFileByIdAuth(@Param("memberId") Long memberId,
                                                        @Param("fileAnalysisId") Long fileAnalysisId);

    @Query("select far from FileAnalysisResult far join fetch far.memberFileLog fl where fl.id = :memberFileLogId")
    List<FileAnalysisResult> findResultFileByFileLogId(@Param("memberFileLogId") Long memberFileLogId);
}
