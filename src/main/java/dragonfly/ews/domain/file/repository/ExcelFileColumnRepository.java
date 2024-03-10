package dragonfly.ews.domain.file.repository;


import dragonfly.ews.domain.file.domain.ExcelFileColumn;
import jakarta.persistence.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExcelFileColumnRepository extends JpaRepository<ExcelFileColumn, Long> {
    List<ExcelFileColumn> findByExcelMemberFileId(Long excelMemberFileId);

    @Query("SELECT e FROM ExcelFileColumn e JOIN e.excelMemberFile emf " +
            "JOIN emf.memberFileLogs mfl " +
            "WHERE mfl.id = :memberFileLogId")
    List<ExcelFileColumn> findByMemberFileLogId(@Param("memberFileLogId") Long memberFileLogId);

    List<ExcelFileColumn> findByIdIn(List<Long> ids);
}
