package dragonfly.ews.domain.result.domain;

import dragonfly.ews.domain.base.BaseTimeEntity;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
public class AnalysisResult extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "analysis_result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_log_id")
    private MemberFileLog memberFileLog;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "analysisResult")
    private List<AnalysisResultFile> analysisResultFiles = new ArrayList<>();

    @Lob
    private String description;

    // 분석 상태
    @Enumerated(EnumType.STRING)
    private AnalysisStatus analysisStatus;

    public AnalysisResult(MemberFileLog memberFileLog, AnalysisStatus analysisStatus) {
        addMemberFileLog(memberFileLog);
        setAnalysisStatus(analysisStatus);
    }

    public void addResultFile(String filename, String savedFilename) {
        AnalysisResultFile analysisResultFile = new AnalysisResultFile(this, filename, savedFilename);
        this.analysisResultFiles.add(analysisResultFile);
    }

    public void addResultFile(String savedFilename) {
        AnalysisResultFile analysisResultFile = new AnalysisResultFile(this, savedFilename);
        this.analysisResultFiles.add(analysisResultFile);
    }

    public void changeAnalysisStatus(AnalysisStatus status) {
        this.analysisStatus = status;
    }

    // == 연관관계 편의 메소드
    protected void addMemberFileLog(MemberFileLog memberFileLog) {
        memberFileLog.getAnalysisResults().add(this);
        setMemberFileLog(memberFileLog);
    }

    public void changeDescription(String description) {
        this.description = description;
    }
}
