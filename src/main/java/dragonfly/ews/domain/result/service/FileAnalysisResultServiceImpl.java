package dragonfly.ews.domain.result.service;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.FileAnalysisResult;
import dragonfly.ews.domain.result.repository.FileAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * TODO 예외메시지 국제화
 */
@Service
@RequiredArgsConstructor
public class FileAnalysisResultServiceImpl implements FileAnalysisResultService {
    private final MemberRepository memberRepository;
    private final MemberFileLogRepository memberFileLogRepository;
    private final FileAnalysisResultRepository fileAnalysisResultRepository;
    private final WebClient webClient;
    private final AnalysisResultProcessor analysisResultProcessor;
    @Value("${file.dir}")
    private String fileDir;

    @Value("${analysis.server.analysis-uri}")
    private String analysisUri;

    @Override
    public Long analysis(Long memberId, Long fileLogId) {
        // 파일 로그 조회
        MemberFileLog memberFileLog = memberFileLogRepository.findMemberFileLogByIdAuth(memberId, fileLogId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
        FileAnalysisResult fileAnalysisResult = new FileAnalysisResult(memberFileLog, AnalysisStatus.PROCESSING);
        fileAnalysisResultRepository.save(fileAnalysisResult);

        // 파일 가져오기
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        MultipartBodyBuilder.PartBuilder file = multipartBodyBuilder.part("file",
                new FileSystemResource(fileDir + memberFileLog.getSavedName()));
        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();

        // 비동기 요청 보내기
        // HTML 파일은 분석 서버에서 "문자열"로 반환함
        webClient.post()
                .uri(analysisUri)
                .bodyValue(multipartBody)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(result ->
                        analysisResultProcessor.processResult((String) result, (Long) fileAnalysisResult.getId()));

        return fileAnalysisResult.getId();
    }

    @Override
    public AnalysisStatus checkAnalysisStatus(Long memberId, Long fileAnalysisResultId) {
        FileAnalysisResult fileAnalysisResult = fileAnalysisResultRepository.findResultFileByIdAuth(memberId,
                        fileAnalysisResultId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
        return fileAnalysisResult.getAnalysisStatus();
    }

    @Override
    public FileAnalysisResult findByResultId(Long memberId, Long fileAnalysisResultId) {
        return fileAnalysisResultRepository.findResultFileByIdAuth(memberId,
                        fileAnalysisResultId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
    }

    @Override
    public List<FileAnalysisResult> findByFileLogId(Long memberId, Long memberFileLogId) {
        // 해당 유저가 파일 로그에 접근할 권한이 있는지 인증
        memberFileLogRepository.findMemberFileLogByIdAuth(memberId, memberFileLogId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));

        return fileAnalysisResultRepository.findResultFileByFileLogId(memberFileLogId);
    }
}
