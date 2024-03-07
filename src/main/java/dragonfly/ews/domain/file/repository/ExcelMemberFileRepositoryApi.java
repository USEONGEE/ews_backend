package dragonfly.ews.domain.file.repository;

import dragonfly.ews.domain.file.domain.ExcelMemberFile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExcelMemberFileRepositoryApi extends JpaRepository<ExcelMemberFile, Long> {
//    @Query("select emf from ExcelMemberFile emf left join fetch emf.columns c " +
//            "left join fetch emf.memberFileLogs mfl " +
//            "where emf.id = :excelMemberFileId")
//    Optional<ExcelMemberFile> findByIdContainColumnAndLogs(@Param("excelMemberFileId") Long excelMemberFileId);

    @EntityGraph(attributePaths = {"columns", "memberFileLogs"})
    @Query("select emf from ExcelMemberFile emf where emf.id = :excelMemberFileId")
    Optional<ExcelMemberFile> findByIdContainColumnAndLogs(@Param("excelMemberFileId") Long excelMemberFileId);
}
