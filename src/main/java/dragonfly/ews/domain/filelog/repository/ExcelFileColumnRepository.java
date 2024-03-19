package dragonfly.ews.domain.filelog.repository;


import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExcelFileColumnRepository extends JpaRepository<ExcelFileColumn, Long> {
    List<ExcelFileColumn> findByIdIn(List<Long> ids);
}
