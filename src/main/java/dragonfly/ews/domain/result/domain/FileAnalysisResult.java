package dragonfly.ews.domain.result.domain;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileAnalysisResult {
    @Id
    @GeneratedValue
    @Column(name = "file_analysis_result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_log_id")
    private MemberFileLog memberFileLog;
    private String savedName;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus analysisStatus;

    public FileAnalysisResult(MemberFileLog memberFileLog, AnalysisStatus analysisStatus) {
        addMemberFileLog(memberFileLog);
        this.analysisStatus = analysisStatus;
    }

    // == 연관관계 편의 메소드
    private void addMemberFileLog(MemberFileLog memberFileLog) {
        memberFileLog.getFileAnalysisResults().add(this);
        this.memberFileLog = memberFileLog;
    }
}
