package dragonfly.ews.domain.project.repository;

import dragonfly.ews.domain.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("select p from Project p where p.id = :projectId and p.owner.id = :ownerId")
    Optional<Project> findByIdAuth(@Param("ownerId") Long ownerId, @Param("projectId") Long projectId);

    @Query("select p from Project p where p.owner.id = :ownerId")
    List<Project> findAll(@Param("ownerId") Long ownerId);

    @Query("select p from Project p left join fetch p.memberFiles mf where p.id = :projectId and p.owner.id = :ownerId")
    Optional<Project> findByIdAndOwnerId(@Param("projectId") Long projectId, @Param("ownerId") Long ownerId);
}
