package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import dragonfly.ews.domain.filelog.exception.NoSuchMemberFileLogException;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcelMemberFileLogService {
    private final ExcelMemberFileLogRepository excelMemberFileLogRepository;

    // TODO validation
    public ExcelMemberFileLog findOne(Long memberId, Long excelMemberFileLogId) {
        ExcelMemberFileLog excelMemberFileLog = excelMemberFileLogRepository.findByIdContainColumn(excelMemberFileLogId)
                .orElseThrow(NoSuchMemberFileLogException::new);
        return excelMemberFileLog;
    }
}
