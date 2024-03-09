package dragonfly.ews.domain.result.repository;

import dragonfly.ews.domain.result.domain.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    @Query("SELECT far FROM AnalysisResult far WHERE far.id = :analysisResultId AND " +
            "(far.memberFileLog.memberFile.owner.id = :memberId OR " +
            "far.memberFileLog.memberFile.project.id IN " +
            "(SELECT pp.project.id FROM ParticipateProject pp WHERE pp.member.id = :memberId))")
    Optional<AnalysisResult> findByIdAuth(@Param("memberId") Long memberId,
                                          @Param("analysisResultId") Long analysisResultId);

    @Query("select far from AnalysisResult far join fetch far.memberFileLog fl where fl.id = :memberFileLogId")
    List<AnalysisResult> findResultFileByFileLogId(@Param("memberFileLogId") Long memberFileLogId);
}
