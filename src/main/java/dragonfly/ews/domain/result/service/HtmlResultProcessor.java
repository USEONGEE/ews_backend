package dragonfly.ews.domain.result.service;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.FileAnalysisResult;
import dragonfly.ews.domain.result.repository.FileAnalysisResultRepository;
import jakarta.persistence.EntityNotFoundException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class HtmlResultProcessor implements AnalysisResultProcessor<String, Long> {
    private final FileAnalysisResultRepository repository;
    private final String EXT = ".html";

    @Value("${file.dir}")
    private String fileDir;

    @Transactional
    @Override
    public void processResult(String htmlContent, Long id) {
        log.info("[HtmlResultProcessor] 처리 중");
        // FileAnalysisResult 엔티티 조회
        FileAnalysisResult fileAnalysisResult = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("FileAnalysisResult 엔티티를 찾을 수 없습니다."));

        // 저장할 파일 이름 생성 및 저장
        String savedFilename = UUID.randomUUID() + EXT;
        fileAnalysisResult.changeSavedName(savedFilename);
        fileAnalysisResult.changeAnalysisStatus(AnalysisStatus.COMPLETE);

        // 파일 저장
        try {
            Path path = Paths.get(fileDir + savedFilename);
            Files.writeString(path, htmlContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            fileAnalysisResult.changeSavedName(null);
            fileAnalysisResult.changeAnalysisStatus(AnalysisStatus.CANCEL);
            repository.flush();
            throw new RuntimeException("HtmlResultProcessor.processResult: 파일 저장에 오류가 발생했습니다.", e);
        } finally {
            log.info("[HtmlResultProcessor] 처리 완료");
        }
    }
}
