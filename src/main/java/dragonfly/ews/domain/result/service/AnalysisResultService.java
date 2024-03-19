package dragonfly.ews.domain.result.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dragonfly.ews.develop.aop.LogMethodParams;
import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.filelog.repository.ExcelFileColumnRepository;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.exception.NoSuchMemberFileLogException;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.ExcelAnalysisResult;
import dragonfly.ews.domain.result.dto.AnalysisExcelFileColumnDto;
import dragonfly.ews.domain.result.dto.ExcelFileAnalysisRequestDto;
import dragonfly.ews.domain.result.dto.UserAnalysisRequestDto;
import dragonfly.ews.domain.result.exceptioon.NotCompletedException;
import dragonfly.ews.domain.result.postprocessor.AnalysisPostProcessor;
import dragonfly.ews.domain.result.repository.AnalysisResultRepository;
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

/**
 * TODO 예외메시지 국제화
 * TODO WebClient 예외 처리 4xx, 5xx
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalysisResultService {
    private final ExcelFileColumnRepository excelFileColumnRepository;
    private final MemberFileLogRepository memberFileLogRepository;
    private final AnalysisResultRepository analysisResultRepository;
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

    /**
     * [데이터 분석 요청]
     *
     * @param memberId
     * @return
     */
    // TODO ExcelAnalysisResultService 으로 옮기는 것을 고려해야함
    @Transactional
    @LogMethodParams
    public AnalysisResult analysis(Long memberId, UserAnalysisRequestDto userAnalysisRequestDto) {
        // 분석 파일 로그 조회
        MemberFileLog memberFileLog = memberFileLogRepository.findByIdAuth(memberId, userAnalysisRequestDto.getMemberFileLogId())
                .orElseThrow(() -> new IllegalStateException("해당 파일을 찾을 수 없습니다."));
        AnalysisResult analysisResult = new ExcelAnalysisResult(memberFileLog, AnalysisStatus.PROCESSING);
        analysisResult.changeDescription(userAnalysisRequestDto.getDescription());

        // 요청 메타데이터 생성
        FileExtension extension = memberFileRepository.findExtensionByMemberFileLogId(
                userAnalysisRequestDto.getMemberFileLogId()
        ).orElseThrow(NoSuchMemberFileLogException::new);
        ExcelFileAnalysisRequestDto analysisRequestDto = new ExcelFileAnalysisRequestDto(extension,
                userAnalysisRequestDto.isAll());

        // column 추가
        if (!userAnalysisRequestDto.isAll()) {
            List<AnalysisExcelFileColumnDto> analysisExcelFileColumnDtos
                    = fetchColumnDtos(userAnalysisRequestDto.getSelectedColumnIds());
            for (AnalysisExcelFileColumnDto analysisExcelFileColumnDto : analysisExcelFileColumnDtos) {
                analysisRequestDto.addColumns(analysisExcelFileColumnDto);
            }
        }

        // targetColumn 추가
        List<AnalysisExcelFileColumnDto> analysisExcelFileColumnDtos
                = fetchColumnDtos(userAnalysisRequestDto.getTargetColumnIds());
        for (AnalysisExcelFileColumnDto analysisExcelFileColumnDto : analysisExcelFileColumnDtos) {
            analysisRequestDto.addTargetColumns(analysisExcelFileColumnDto);
        }

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
                .subscribe(result ->
                        analysisPostProcessor.success(result, analysisResult.getId()));

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


}
