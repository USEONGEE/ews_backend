package dragonfly.ews.domain.result.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dragonfly.ews.develop.aop.LogMethodParams;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.exception.NoSuchMemberFileLogException;
import dragonfly.ews.domain.filelog.repository.ExcelFileColumnRepository;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.domain.AnalysisResultToken;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.ExcelAnalysisResult;
import dragonfly.ews.domain.result.dto.AnalysisExcelFileColumnDto;
import dragonfly.ews.domain.result.dto.ExcelFileAnalysisRequestDto;
import dragonfly.ews.domain.result.dto.UserAnalysisRequestDto;
import dragonfly.ews.domain.result.exceptioon.NotCompletedException;
import dragonfly.ews.domain.result.postprocessor.AnalysisPostProcessor;
import dragonfly.ews.domain.result.repository.AnalysisResultRepository;
import dragonfly.ews.domain.result.repository.AnalysisResultTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

/**
 * 분석 서버에 요청을 보낸 후 응답은 callbackUrl로 받음
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalysisResultAsyncService implements AnalysisResultService {
    private final ExcelFileColumnRepository excelFileColumnRepository;
    private final MemberFileLogRepository memberFileLogRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisResultTokenRepository analysisResultTokenRepository;
    private final WebClient webClient;
    private final AnalysisPostProcessor analysisPostProcessor;
    private final MemberFileRepository memberFileRepository;
    private final ObjectMapper objectMapper;
    private final FileUtils fileUtils;
    @Value("${file.dir}")
    private String fileDir;

    @Value("${analysis.server.url}")
    private String analysisServerUri;
    @Value("${analysis.server.analysis-uri}")
    private String analysisUri;

    @Value("${server.url}")
    private String serverUrl;

    /**
     * [데이터 분석 요청]
     *
     * @param memberId
     * @return
     */
    // TODO ExcelAnalysisResultService 으로 옮기는 것을 고려해야함
    // TODO MemberFileLog가 ExcelMemberFileLog 라면 ExcelFileColumn을 갖고 있기 때문에 한방 쿼리로 가능
    // TODO 분석 요청을 보낸 후에 그냥 끊고, 분석 서버에서 현재 서버에 POST 요청을 보내도록
    @Transactional
    @LogMethodParams
    public AnalysisResult analysis(Long memberId, UserAnalysisRequestDto userAnalysisRequestDto) {
        // 분석 파일 로그 조회
        MemberFileLog memberFileLog = memberFileLogRepository.findByIdAuth(memberId, userAnalysisRequestDto.getMemberFileLogId())
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
        AnalysisResult analysisResult = new ExcelAnalysisResult(memberFileLog, AnalysisStatus.CREATED);
        analysisResult.changeDescription(userAnalysisRequestDto.getDescription());
        analysisResultRepository.save(analysisResult);

        // redis에 요청 토큰 값 저장
        AnalysisResultToken analysisResultToken = AnalysisResultToken.builder()
                .id(analysisResult.getId())
                .token(UUID.randomUUID().toString())
                .expiration(3600L) // 1시간 = 3600초
                .build();
        analysisResultTokenRepository.save(analysisResultToken);

        //// 메타 데이터 생성
        // 요청 메타데이터 생성
        FileExtension extension = memberFileRepository.findExtensionByMemberFileLogId(
                userAnalysisRequestDto.getMemberFileLogId()
        ).orElseThrow(NoSuchMemberFileLogException::new);
        // redis token Id 추가
        String redisKey = analysisResultToken.getRedisKey();
        // callback Url 생성
        String callbackUrl = createCallbackUrl(analysisResultToken.getId());
        // excel column 추가
        List<AnalysisExcelFileColumnDto> analysisExcelFileColumnDtos
                = fetchColumnDtos(userAnalysisRequestDto.getSelectedColumnIds());
        // targetColumn 추가
        List<AnalysisExcelFileColumnDto> targetColumnDtos
                = fetchColumnDtos(userAnalysisRequestDto.getTargetColumnIds());

        ExcelFileAnalysisRequestDto analysisRequestDto = new ExcelFileAnalysisRequestDto(extension,
                callbackUrl,
                redisKey,
                analysisExcelFileColumnDtos,
                targetColumnDtos);

        // 파일 내용 JSON 문자열로 파싱
        String json = null;
        try {
            json = objectMapper.writeValueAsString(analysisRequestDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 파일 가져오기
        String savedFilename = memberFileLog.getSavedName();
        String fullPath = fileUtils.getFullPath(savedFilename);

        // body 작성
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(fullPath));
        builder.part("metadata", json);
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();

        // 비동기 요청 보내기
        // HTML 파일은 분석 서버에서 "문자열"로 반환함
        webClient.post()
                .uri(analysisServerUri + analysisUri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> analysisPostProcessor.fail((Exception) error, analysisResult.getId()))
                .subscribe();

        analysisResult.changeAnalysisStatus(AnalysisStatus.PROCESSING);
        return analysisResult;
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
    public List<AnalysisResult> findByMemberFileLogId(Long memberId, Long memberFileLogId) {
        // 해당 유저가 파일 로그에 접근할 권한이 있는지 인증
        memberFileLogRepository.findByIdAuth(memberId, memberFileLogId)
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));

        return analysisResultRepository.findResultFileByFileLogId(memberFileLogId);
    }

    /**
     * [분석 완료된 파일 조회]
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


    private List<AnalysisExcelFileColumnDto> fetchColumnDtos(List<Long> excelFileColumnIds) {
        List<ExcelFileColumn> excelFileColumns = excelFileColumnRepository.findByIdIn(excelFileColumnIds);
        return excelFileColumns.stream()
                .map(AnalysisExcelFileColumnDto::of)
                .toList();
    }

    private String createCallbackUrl(Object id) {
        return String.format("%sanalysis/excel/result/callback/%s", serverUrl, id);
    }

}
