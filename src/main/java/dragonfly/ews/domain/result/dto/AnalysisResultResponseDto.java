package dragonfly.ews.domain.result.dto;

import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import lombok.Data;

@Data
public class AnalysisResultResponseDto {
    private Long id;
    private AnalysisStatus analysisStatus;

    public AnalysisResultResponseDto(AnalysisResult analysisResult) {
        this.id = analysisResult.getId();
        this.analysisStatus = analysisResult.getAnalysisStatus();
    }
}
