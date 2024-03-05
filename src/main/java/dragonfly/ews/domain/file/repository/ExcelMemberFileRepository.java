package dragonfly.ews.domain.file.repository;

import dragonfly.ews.domain.file.domain.ExcelMemberFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcelMemberFileRepository extends JpaRepository<ExcelMemberFile, Long> {
}
