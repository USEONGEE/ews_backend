package dragonfly.ews.domain.result.dto;

import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import lombok.Data;

@Data
public class AnalysisStatusResponseDto {
    private Long id;
    private AnalysisStatus analysisStatus;

    public AnalysisStatusResponseDto(AnalysisResult analysisResult) {
        this.id = analysisResult.getId();
        this.analysisStatus = analysisResult.getAnalysisStatus();
    }

}
