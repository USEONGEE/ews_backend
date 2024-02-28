package dragonfly.ews.domain.university.repository;

import dragonfly.ews.domain.university.domian.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    Optional<University> findByName(String name);
    Optional<University> findByNameAndCreatedDate(String name, String createdDate);
}
