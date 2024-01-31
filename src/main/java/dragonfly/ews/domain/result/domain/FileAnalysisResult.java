package dragonfly.ews.domain.result.domain;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import jakarta.persistence.*;

@Entity
public class FileAnalysisResult {
    @Id
    @GeneratedValue
    @Column(name = "file_analysis_result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_log_id")
    private MemberFileLog memberFileLog;
    private String savedName;
}
