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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileAnalysisResultServiceImpl implements FileAnalysisResultService {
    private final MemberRepository memberRepository;
    private final MemberFileLogRepository memberFileLogRepository;
    private final FileAnalysisResultRepository fileAnalysisResultRepository;
    private final WebClient webClient;
    @Value("${file.dir}")
    private String fileDir;

    @Value("${analysis.server.analysis-uri}")
    private String analysisUri;

    @Override
    public Long analysis(Long memberId, Long fileLogId) {

        Optional<MemberFileLog> fileLog =
                memberFileLogRepository.findMemberFileLogByIdAuth(memberId, fileLogId);
        MemberFileLog memberFileLog = fileLog.orElseThrow(() -> new IllegalStateException("파일로그를 찾을 수 없습니다."));
        FileAnalysisResult fileAnalysisResult = new FileAnalysisResult(memberFileLog, AnalysisStatus.PROCESSING);
        fileAnalysisResultRepository.save(fileAnalysisResult);

        // 파일 가져오기
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        MultipartBodyBuilder.PartBuilder file = multipartBodyBuilder.part("file",
                new FileSystemResource(fileDir + memberFileLog.getSavedName()));
        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();


        webClient.post()
                .uri(analysisUri)
                .body(multipartBody)
                .subscribe();

    }

    @Override
    public AnalysisStatus checkAnalysisStatus(Long memberId, Long fileAnalysisResultId) {
        return null;
    }

    @Override
    public FileAnalysisResult findByResultId(Long memberId, Long fileAnalysisResultId) {
        return null;
    }

    @Override
    public List<FileAnalysisResult> findByFileLogId(Long memberId, Long fileLogId) {
        return null;
    }
}
