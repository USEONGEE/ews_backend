package dragonfly.ews.domain.result.repository;

import dragonfly.ews.domain.result.domain.ExcelAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExcelAnalysisResultRepository extends JpaRepository<ExcelAnalysisResult, Long> {

    @Query("select ear from ExcelAnalysisResult ear join fetch ear.analysisResultFiles arf where ear.id = :excelAnalysisResultId")
    Optional<ExcelAnalysisResult> findByIdContainResultFile(@Param("excelAnalysisResultId") Long excelAnalysisResultId);
}
