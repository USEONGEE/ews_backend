package dragonfly.ews.domain.file.repository;


import dragonfly.ews.domain.file.domain.ExcelFileColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExcelFileColumnRepository extends JpaRepository<ExcelFileColumn, Long> {
    List<ExcelFileColumn> findByExcelMemberFileId(Long excelMemberFileId);
}
