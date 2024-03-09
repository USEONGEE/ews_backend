package dragonfly.ews.domain.result.service;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.exceptioon.CannotProcessAnalysisException;
import dragonfly.ews.domain.result.exceptioon.NotCompletedException;
import dragonfly.ews.domain.result.repository.AnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * TODO 예외메시지 국제화
 * TODO WebClient 예외 처리 4xx, 5xx
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalysisResultService {
    private final MemberFileLogRepository memberFileLogRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final WebClient webClient;
    private final AnalysisResultProcessor analysisResultProcessor;
    @Value("${file.dir}")
    private String fileDir;

    @Value("${analysis.server.url}")
    private String analysisServerUri;
    @Value("${analysis.server.analysis-uri}")
    private String analysisUri;

    @Transactional
    public AnalysisResult createAnalysisResult(Long memberId, Long memberFileLogId) {
        MemberFileLog memberFileLog = memberFileLogRepository.findByIdAuth(memberId, memberFileLogId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
        AnalysisResult analysisResult = new AnalysisResult(memberFileLog, AnalysisStatus.CREATED);
        return analysisResultRepository.save(analysisResult);
    }

    /**
     * [데이터 분석 요청]
     *
     * @param memberId
     * @param analysisResultId
     * @return
     */
    @Transactional
    public boolean analysis(Long memberId, Long analysisResultId) {

        // 분석 파일 로그 조회
        AnalysisResult analysisResult = analysisResultRepository.findByIdAuth(memberId,
                        analysisResultId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
        validateFileAnalysisStatus(analysisResult);

        // 파일 가져오기
        MemberFileLog memberFileLog = analysisResult.getMemberFileLog();
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new FileSystemResource(fileDir + memberFileLog.getSavedName()));
        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();

        // 비동기 요청 보내기
        // HTML 파일은 분석 서버에서 "문자열"로 반환함
        webClient.post()
                .uri(analysisServerUri + analysisUri)
                .bodyValue(multipartBody)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(result ->
                        analysisResultProcessor.processResult((String) result, (Long) analysisResult.getId()));

        analysisResult.changeAnalysisStatus(AnalysisStatus.PROCESSING);
        return true;
    }

    private void validateFileAnalysisStatus(AnalysisResult analysisResult) {
        switch (analysisResult.getAnalysisStatus()) {
            case CANCELED -> throw new CannotProcessAnalysisException("분석 요청이 취소된 파일입니다. 관리자에게 문의하세요");
            case PROCESSING -> throw new CannotProcessAnalysisException("이미 분석 중인 파일입니다.");
            case COMPLETED -> throw new CannotProcessAnalysisException("이미 분석된 파일입니다.");
        }
    }

    /**
     * [단건 조회]
     *
     * @param memberId
     * @param analysisResultId
     * @return
     */
    public AnalysisResult findByResultId(Long memberId, Long analysisResultId) {
        return analysisResultRepository.findByIdAuth(memberId, analysisResultId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
    }

    /**
     * [전체 조회]
     *
     * @param memberId
     * @param memberFileLogId
     * @return
     */
    public List<AnalysisResult> findByFileLogId(Long memberId, Long memberFileLogId) {
        // 해당 유저가 파일 로그에 접근할 권한이 있는지 인증
        memberFileLogRepository.findByIdAuth(memberId, memberFileLogId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));

        return analysisResultRepository.findResultFileByFileLogId(memberFileLogId);
    }

    /**
     * []
     *
     * @param memberId
     * @param analysisResultId
     * @return
     */
    public AnalysisResult findCompletedFileById(Long memberId, Long analysisResultId) {
        AnalysisResult analysisResult = analysisResultRepository.findByIdAuth(memberId, analysisResultId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
        if (analysisResult.getAnalysisStatus() != AnalysisStatus.COMPLETED) {
            throw new NotCompletedException("분석이 완료된 파일이 아닙니다.");
        }
        return analysisResult;
    }
}
