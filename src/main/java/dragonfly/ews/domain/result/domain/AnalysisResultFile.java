package dragonfly.ews.domain.result.domain;

import dragonfly.ews.domain.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(value = AccessLevel.PROTECTED)
public class AnalysisResultFile extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String filename;
    private String savedName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id")
    private AnalysisResult analysisResult;

    public AnalysisResultFile(AnalysisResult excelAnalysisResult, String savedName) {
        this.savedName = savedName;
        this.filename = "file";
        this.analysisResult = excelAnalysisResult;
    }

    public AnalysisResultFile(AnalysisResult excelAnalysisResult, String fileName, String savedName) {
        this.savedName = savedName;
        this.filename = fileName;
        this.analysisResult = excelAnalysisResult;
    }
}
