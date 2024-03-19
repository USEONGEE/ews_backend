package dragonfly.ews.domain.filelog.repository;

import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExcelFIleLogRepository extends JpaRepository<ExcelFileColumn, Long> {
    List<ExcelFileColumn> findByIdIn(List<Long> ids);
}
