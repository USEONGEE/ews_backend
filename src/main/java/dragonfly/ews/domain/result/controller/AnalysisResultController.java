package dragonfly.ews.domain.result.controller;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.domain.AnalysisResultFile;
import dragonfly.ews.domain.result.dto.AnalysisStatusResponseDto;
import dragonfly.ews.domain.result.dto.AnalysisExcelFileColumnDto;
import dragonfly.ews.domain.result.dto.UserAnalysisRequestDto;
import dragonfly.ews.domain.result.repository.AnalysisResultFileRepository;
import dragonfly.ews.domain.result.service.AnalysisResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisResultController {
    private final AnalysisResultService analysisResultService;
    private final AnalysisResultFileRepository analysisResultFileRepository;
    @Value("${file.dir}")
    private String fileDir;

    /**
     * [분석 요청된 파일의 상태 조회]
     *
     * @param fileAnalysisId
     * @param member
     * @return
     */
    @GetMapping("/status/{fileAnalysisId}")
    public ResponseEntity<SuccessResponse> checkAnalysisStatus(
            @PathVariable("fileAnalysisId") Long fileAnalysisId,
            @AuthenticationPrincipal(expression = "member") Member member) {
        AnalysisResult analysisResult = analysisResultService.findByResultId(member.getId(),
                fileAnalysisId);
        AnalysisStatusResponseDto analysisStatusResponseDto = new AnalysisStatusResponseDto(analysisResult);
        return new ResponseEntity<>(SuccessResponse.of(analysisStatusResponseDto), HttpStatus.OK);
    }

    /**
     * [파일 분석 요청]
     *
     * @param member
     * @return
     */
    @PostMapping
    public ResponseEntity<SuccessResponse> analysis(
            @RequestBody @Valid UserAnalysisRequestDto userAnalysisRequestDto,
            @AuthenticationPrincipal(expression = "member") Member member) {
        AnalysisResult analysis = analysisResultService.analysis(member.getId(),
                userAnalysisRequestDto);
        return new ResponseEntity<>(SuccessResponse.of(analysis.getId()), HttpStatus.OK);
    }

    /**
     * [분석된 파일 응답]
     * @param analysisResultFileId
     * @param member
     * @return
     */
    @GetMapping("/{analysisResultFileId}")
    public ResponseEntity downloadAnalysisFile(
            @PathVariable("analysisResultFileId") Long analysisResultFileId,
            @AuthenticationPrincipal(expression = "member") Member member) {

        // TODO 인증 기능이 없어서 나중에 인증 기능 추가하자
        AnalysisResultFile analysisResultFile = analysisResultFileRepository.findById(analysisResultFileId)
                .orElseThrow(NoSuchFileException::new);

        try {
            UrlResource resource = new UrlResource("file:" + fileDir + analysisResultFile.getSavedName());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML) // HTML 파일의 MIME 타입
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

}
