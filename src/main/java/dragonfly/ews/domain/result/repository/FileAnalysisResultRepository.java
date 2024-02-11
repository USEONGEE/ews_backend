package dragonfly.ews.domain.result.repository;

import dragonfly.ews.domain.result.domain.FileAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileAnalysisResultRepository extends JpaRepository<FileAnalysisResult, Long> {

    @Query("select far from FileAnalysisResult far where far.id = :fileAnalysisId and " +
            "far.memberFileLog.memberFile.owner.id = :memberId")
    Optional<FileAnalysisResult> findResultFileByIdAuth(@Param("memberId") Long memberId,
                                                        @Param("fileAnalysisId") Long fileAnalysisId);

    @Query("select far from FileAnalysisResult far join fetch far.memberFileLog fl where fl.id = :memberFileLogId")
    List<FileAnalysisResult> findResultFileByFileLogId(@Param("memberFileLogId") Long memberFileLogId);
}
