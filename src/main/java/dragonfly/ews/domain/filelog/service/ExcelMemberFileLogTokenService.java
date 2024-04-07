package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLogToken;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ExcelMemberFileLogTokenService {
    private final ExcelMemberFileLogTokenRepository excelMemberFileLogTokenRepository;

    @Transactional
    public void validateAndDeleteToken(Long excelMemberFileLogId, String token) {
        ExcelMemberFileLogToken excelMemberFileLogToken = excelMemberFileLogTokenRepository.findById(excelMemberFileLogId)
                .orElseThrow(NoSuchElementException::new);
        if (!token.equals(excelMemberFileLogToken.getToken())) {
            throw new IllegalArgumentException("토큰값이 다릅니다");
        }

        excelMemberFileLogTokenRepository.delete(excelMemberFileLogToken);
    }
}