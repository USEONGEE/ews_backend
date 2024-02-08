package dragonfly.ews.domain.result.repository;

import dragonfly.ews.domain.result.domain.FileAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAnalysisResultRepository extends JpaRepository<FileAnalysisResult, Long> {

}
