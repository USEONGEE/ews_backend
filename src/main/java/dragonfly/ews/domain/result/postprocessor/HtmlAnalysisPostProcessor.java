package dragonfly.ews.domain.result.postprocessor;

import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.ExcelAnalysisResult;
import dragonfly.ews.domain.result.exceptioon.CannotProcessAnalysisException;
import dragonfly.ews.domain.result.repository.ExcelAnalysisResultRepository;
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
import java.util.UUID;

/**
 * [1개의 HTML 파일 처리]
 */
//@Service
@RequiredArgsConstructor
@Slf4j
public class HtmlAnalysisPostProcessor implements AnalysisPostProcessor<String, Long> {
    private final ExcelAnalysisResultRepository repository;
    private final String EXT = ".html";

    @Value("${file.dir}")
    private String fileDir;

    @Transactional
    @Override
    public void success(String htmlContent, Long id) {
        // AnalysisResult 엔티티 조회
        ExcelAnalysisResult excelAnalysisResult = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("AnalysisResult 엔티티를 찾을 수 없습니다."));

        // 저장할 파일 이름 생성 및 저장
        String savedFilename = UUID.randomUUID() + EXT;
        excelAnalysisResult.addResultFile(savedFilename);
        excelAnalysisResult.changeAnalysisStatus(AnalysisStatus.COMPLETED);

        // 파일 저장
        try {
            Path path = Paths.get(fileDir + savedFilename);
            Files.writeString(path, htmlContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            excelAnalysisResult.changeAnalysisStatus(AnalysisStatus.CANCELED);
            repository.flush();
            throw new RuntimeException("파일 저장에 오류가 발생했습니다.", e);
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
