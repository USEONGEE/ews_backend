package dragonfly.ews.domain.filelog.dto;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberFileLogContainResultsResponseDto {
    private Long id;
    private String description;
    private LocalDateTime createdData;
    private List<AnalysisResultResponse> results = new ArrayList<>();

    public MemberFileLogContainResultsResponseDto(MemberFileLog memberFileLog) {
        this.id = memberFileLog.getId();
        this.createdData = memberFileLog.getCreatedDate();
        this.description = memberFileLog.getDescription();
        for (AnalysisResult analysisResult : memberFileLog.getAnalysisResults()) {
            this.results.add(new AnalysisResultResponse(analysisResult));
        }
    }

    @Data

    private static class AnalysisResultResponse {
        private Long id;
        private AnalysisStatus status;
        private LocalDateTime createdDate;

        public AnalysisResultResponse(AnalysisResult analysisResult) {
            this.id = analysisResult.getId();
            this.status = analysisResult.getAnalysisStatus();
            this.createdDate = analysisResult.getMemberFileLog().getCreatedDate();
        }
    }

}
