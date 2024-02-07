package dragonfly.ews.domain.file.repository;

import dragonfly.ews.domain.file.domain.MemberFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberFileRepository extends JpaRepository<MemberFile, Long> {
}
