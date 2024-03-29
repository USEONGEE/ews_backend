package dragonfly.ews.domain.result.service;

import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.dto.UserAnalysisRequestDto;

import java.util.List;

public interface AnalysisResultService {
    AnalysisResult analysis(Long memberId, UserAnalysisRequestDto userAnalysisRequestDto);

    AnalysisResult findByResultId(Long memberId, Long analysisResultId);

    List<AnalysisResult> findByMemberFileLogId(Long memberId, Long memberFileLogId);

    AnalysisResult findCompletedFileById(Long memberId, Long analysisResultId);
}
