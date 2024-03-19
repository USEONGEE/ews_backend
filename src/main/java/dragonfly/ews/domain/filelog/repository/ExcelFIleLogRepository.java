package dragonfly.ews.domain.filelog.repository;

import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcelFIleLogRepository extends JpaRepository<ExcelFileColumn, Long> {

}
