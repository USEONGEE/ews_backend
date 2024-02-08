package dragonfly.ews.domain.project.repository;

import dragonfly.ews.domain.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
