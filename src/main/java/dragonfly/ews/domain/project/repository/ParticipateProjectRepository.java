package dragonfly.ews.domain.project.repository;

import dragonfly.ews.domain.project.domain.Project;
import dragonfly.ews.domain.project.domain.ProjectParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipateProjectRepository extends JpaRepository<ProjectParticipant, Long> {
    @Query("SELECT p.project FROM ProjectParticipant p WHERE p.member.id = :memberId")
    List<Project> findProjectsByParticipantId(@Param("memberId") Long memberId);

    @Query("SELECT p.project FROM ProjectParticipant p WHERE p.member.id = :memberId and p.project.id = :projectId")
    Optional<Project> findProjectByParticipantId(@Param("memberId") Long memberId, @Param("projectId") Long projectId);
}
