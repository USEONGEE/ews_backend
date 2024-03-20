package dragonfly.ews.domain.project.repository;

import dragonfly.ews.domain.project.domain.ProjectParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, Long> {
    @Query("SELECT pp FROM ProjectParticipant pp JOIN FETCH pp.member WHERE pp.project.id = :projectId")
    List<ProjectParticipant> findByProjectIdContainMember(@Param("projectId") Long projectId);

}
