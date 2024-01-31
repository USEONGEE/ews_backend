package dragonfly.ews.domain.filelog.domain;

import dragonfly.ews.domain.file.domain.MemberFile;
import jakarta.persistence.*;

@Entity
public class MemberFileLog {
    @Id
    @GeneratedValue
    @Column(name = "file_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private MemberFile memberFile;
    private String savedName;
}
