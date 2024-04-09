package dragonfly.ews.domain.filelog.validation;

import dragonfly.ews.domain.file.aop.postprocessor.ColumnTypeCheckPostProcessor;
import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLogValidationStep;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLogValidationStepToken;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLogValidationType;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogRepository;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogValidationStepRepository;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogValidationStepTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class ColumnTypeCheckValidationStrategy implements ExcelMemberFileLogValidationStrategy {
    private final ExcelMemberFileLogRepository excelMemberFileLogRepository;
    private final ExcelMemberFileLogValidationStepRepository excelMemberFileLogValidationStepRepository;
    private final ExcelMemberFileLogValidationStepTokenRepository excelMemberFileLogValidationStepTokenRepository;
    private final ColumnTypeCheckPostProcessor columnTypeCheckPostProcessor;
    private final FileUtils memberFileUtils;
    @Value("${analysis.server.url}")
    private String analysisServerUri;
    @Value("${analysis.server.column-check-uri}")
    private String columnCheckUri;
    @Value("${server.url}")
    private String serverUrl;
    private final WebClient webClient;


    @Override
    public void validate(Long memberFileLogId) {
        ExcelMemberFileLog excelMemberFileLog = excelMemberFileLogRepository.findById(memberFileLogId)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일 로그가 존재하지 않습니다."));

        // 열 타입 체크 검증 스텝 생성
        ExcelMemberFileLogValidationStep excelMemberFileLogValidationStep
                = new ExcelMemberFileLogValidationStep(ExcelMemberFileLogValidationType.COLUMN_VALIDATION);
        excelMemberFileLogValidationStepRepository.save(excelMemberFileLogValidationStep);

        // 열 타입 체크 검증 스텝 토큰 생성
        ExcelMemberFileLogValidationStepToken excelMemberFileLogValidationStepToken
                = new ExcelMemberFileLogValidationStepToken(excelMemberFileLogValidationStep.getId(), UUID.randomUUID().toString(), 3600L);
        excelMemberFileLogValidationStepTokenRepository.save(excelMemberFileLogValidationStepToken);

        // 열 타입 체크 요청 메타데이터 생성
        String fullPath = memberFileUtils.getFullPath(excelMemberFileLog.getSavedName());
        String callbackUrl = createCallbackUrl(excelMemberFileLog.getId());
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(fullPath));
        builder.part("callbackUrl", callbackUrl);
        builder.part("redisKey", excelMemberFileLogValidationStepToken.getRedisKey());
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();

        // 열 타입 체크 요청
        webClient.post()
                .uri(analysisServerUri + columnCheckUri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> columnTypeCheckPostProcessor.fail(e, excelMemberFileLog.getMemberFile().getId())) // 예외 발생시 memberfile 제거
                .subscribe();
    }

    private String createCallbackUrl(Object id) {
        return String.format("%sfilelog/excel/columns-type-check/callback/%s", serverUrl, id);
    }
}
