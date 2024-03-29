package dragonfly.ews.domain.result.service;

import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.result.domain.ExcelAnalysisResult;
import dragonfly.ews.domain.result.postprocessor.AnalysisPostProcessor;
import dragonfly.ews.domain.result.repository.AnalysisResultRepository;
import dragonfly.ews.domain.result.repository.ExcelAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExcelAnalysisResultService {
    private final AnalysisResultRepository analysisResultRepository;
    private final ExcelAnalysisResultRepository excelAnalysisResultRepository;
    private final AnalysisPostProcessor analysisPostProcessor;

    public ExcelAnalysisResult findOne(Long memberId, Long excelAnalysisResultId) {
        analysisResultRepository.findByIdAuth(memberId, excelAnalysisResultId)
                .orElseThrow(NoSuchFileException::new);
        return excelAnalysisResultRepository.findByIdContainResultFile(excelAnalysisResultId)
                .orElseThrow(NoSuchFileException::new);
    }

    public boolean handleAnalysisResultCallback(Long analysisResultId, String body) {
        analysisPostProcessor.success(body, analysisResultId);
        return true;
    }

}
