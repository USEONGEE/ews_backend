package dragonfly.ews.domain.result.service;

import dragonfly.ews.domain.result.domain.AnalysisResultToken;
import dragonfly.ews.domain.result.repository.AnalysisResultTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AnalysisResultTokenService {
    private final AnalysisResultTokenRepository analysisResultTokenRepository;

    @Transactional
    public void validateAndDeleteToken(Long excelAnalysisResultId, String token) {
        AnalysisResultToken analysisResultToken = analysisResultTokenRepository.findById(excelAnalysisResultId)
                .orElseThrow(NoSuchElementException::new);
        if (!token.equals(analysisResultToken.getToken())) {
            throw new IllegalArgumentException("토큰값이 다릅니다");
        }

        analysisResultTokenRepository.delete(analysisResultToken);
    }
}