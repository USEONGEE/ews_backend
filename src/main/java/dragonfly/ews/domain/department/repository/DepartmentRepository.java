package dragonfly.ews.domain.department.repository;

import dragonfly.ews.domain.department.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
