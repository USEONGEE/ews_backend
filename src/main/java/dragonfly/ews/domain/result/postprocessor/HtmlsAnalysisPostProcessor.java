package dragonfly.ews.domain.result.postprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.domain.AnalysisResultFile;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.ExcelAnalysisResult;
import dragonfly.ews.domain.result.repository.ExcelAnalysisResultRepository;
import dragonfly.ews.domain.result.exceptioon.CannotProcessAnalysisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

/**
 * [Map<String, String> 으로 제공되는 여러 개의 HTML 파일 처리]
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HtmlsAnalysisPostProcessor implements AnalysisPostProcessor<String, Long> {
    private final ExcelAnalysisResultRepository repository;
    private final ObjectMapper objectMapper;
    private final String EXT = ".html";

    @Value("${file.dir}")
    private String fileDir;
    @Transactional(noRollbackFor = IOException.class)
    @Override
    public void success(String result, Long id) {
        try {
            // JSON 문자열을 Map<String, String>으로 변환
            Map<String, String> htmlFiles = objectMapper.readValue(result, new TypeReference<Map<String, String>>() {});

            // AnalysisResult 엔티티 조회
            ExcelAnalysisResult excelAnalysisResult = repository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("AnalysisResult 엔티티를 찾을 수 없습니다."));

            for (Map.Entry<String, String> entry : htmlFiles.entrySet()) {
                String filename = entry.getKey() + EXT; // 파일 이름 설정
                String htmlContent = entry.getValue(); // HTML 내용

                // 저장할 파일 이름 생성 및 저장
                String savedFilename = UUID.randomUUID() + "_" + filename; // 충돌 방지를 위해 UUID 추가
                excelAnalysisResult.addResultFile(filename, savedFilename);

                // 파일 저장
                Path path = Paths.get(fileDir, savedFilename);
                Files.writeString(path, htmlContent, StandardCharsets.UTF_8);

            }
            excelAnalysisResult.changeAnalysisStatus(AnalysisStatus.COMPLETED);

        } catch (IOException e) {
            log.error("파일 저장 또는 JSON 파싱에 실패했습니다.", e);
            throw new RuntimeException("파일 저장 또는 JSON 파싱에 오류가 발생했습니다.", e);
        }
    }
    @Transactional(noRollbackFor = CannotProcessAnalysisException.class)
    @Override
    public void fail(Exception e, Long id) {
        log.error("[HtmlAnalysisPostProcessor.fail] 호출");
        AnalysisResult analysisResult = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("AnalysisResult 엔티티를 찾을 수 없습니다."));
        analysisResult.changeAnalysisStatus(AnalysisStatus.CANCELED);
        repository.flush();

        throw new CannotProcessAnalysisException("분석 서버에 요청에 실패했습니다.");
    }
}
