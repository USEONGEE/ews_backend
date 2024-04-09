package dragonfly.ews.domain.file.aop.postprocessor;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLogValidationStep;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogValidationStepRepository;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.exceptioon.CannotProcessAnalysisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * [ColumnTypeCheck에 실패한 경우 처리]
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ColumnTypeCheckPostProcessor {
    private final ExcelMemberFileLogValidationStepRepository excelMemberFileLogValidationStepRepository;
    @Transactional(noRollbackFor = CannotProcessAnalysisException.class)
    public void fail(Throwable e, Long id) {
        log.error("[ColumnTypeCheckPostProcessors.fail] 호출");
        ExcelMemberFileLogValidationStep excelMemberFileLogValidationStep = excelMemberFileLogValidationStepRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("AnalysisResult 엔티티를 찾을 수 없습니다."));
        excelMemberFileLogValidationStep.validationFail(e.getMessage(), 0);
    }
}
