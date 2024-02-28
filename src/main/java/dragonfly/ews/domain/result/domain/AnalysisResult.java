package dragonfly.ews.domain.result.domain;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisResult {
    @Id
    @GeneratedValue
    @Column(name = "analysis_result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_log_id")
    private MemberFileLog memberFileLog;
    private String savedName;

    // 분석 상태
    @Enumerated(EnumType.STRING)
    private AnalysisStatus analysisStatus;

    public AnalysisResult(MemberFileLog memberFileLog, AnalysisStatus analysisStatus) {
        addMemberFileLog(memberFileLog);
        this.analysisStatus = analysisStatus;
    }

    /**
     * [결과 파일명 저장]
     * <p/> 외부 서버에서 분석 결과를 받은 후, 파일을 저장.
     * <br/> 파일을 저장하면은 자동으로 분석 상태는 COMPLETE 로 변경
     * @param savedName
     */
    public void changeSavedName(String savedName) {
        changeAnalysisStatus(AnalysisStatus.COMPLETED);
        setSavedName(savedName);
    }

    public void changeAnalysisStatus(AnalysisStatus status) {
        this.analysisStatus = status;
    }

    // == 연관관계 편의 메소드
    private void addMemberFileLog(MemberFileLog memberFileLog) {
        memberFileLog.getAnalysisResults().add(this);
        setMemberFileLog(memberFileLog);
    }
}
