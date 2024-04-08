package dragonfly.ews.domain.filelog.repository;

import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ExcelMemberFileLogRepository extends JpaRepository<ExcelMemberFileLog, Long> {

    @Query("select emfl from ExcelMemberFileLog emfl left join fetch emfl.columns where emfl.id = :excelMemberFileLogId")
    Optional<ExcelMemberFileLog> findByIdContainColumn(@Param(value = "excelMemberFileLogId") Long excelMemberFileLogId);

}
