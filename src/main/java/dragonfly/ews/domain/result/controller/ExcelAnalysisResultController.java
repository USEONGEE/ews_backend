package dragonfly.ews.domain.result.controller;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.develop.aop.LogMethodParams;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.result.domain.AnalysisResultToken;
import dragonfly.ews.domain.result.domain.ExcelAnalysisResult;
import dragonfly.ews.domain.result.dto.ExcelAnalysisResultResponseDto;
import dragonfly.ews.domain.result.repository.AnalysisResultTokenRepository;
import dragonfly.ews.domain.result.service.AnalysisResultTokenService;
import dragonfly.ews.domain.result.service.ExcelAnalysisResultService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis/excel")
public class ExcelAnalysisResultController {
    private final ExcelAnalysisResultService excelAnalysisResultService;
    private final AnalysisResultTokenRepository analysisResultTokenRepository;
    private final AnalysisResultTokenService analysisResultTokenService;

    /**
     * [ExcelAnalysisResult 단건 조회]
     *
     * @param member
     * @param excelAnalysisResultId
     * @return
     */
    @GetMapping("/{excelAnalysisResultId}")
    public ResponseEntity<SuccessResponse> findOne(
            @AuthenticationPrincipal(expression = "member") Member member,
            @PathVariable(value = "excelAnalysisResultId") Long excelAnalysisResultId) {
        ExcelAnalysisResult find = excelAnalysisResultService.findOne(member.getId(), excelAnalysisResultId);
        return new ResponseEntity<>(SuccessResponse.of(ExcelAnalysisResultResponseDto.of(find)), HttpStatus.OK);
    }

    /**
     * [분석 결과 리턴]
     * @param callbackDto
     * @return
     */
    @PostMapping("/result/callback/{excelAnalysisResultId}")
    public ResponseEntity<SuccessResponse> handleAnalysisResultCallback(
           @RequestBody CallbackDto callbackDto,
           @PathVariable(value = "excelAnalysisResultId") Long excelAnalysisResultId
    ) {
        // 토큰에서 추출하고 id 가져오기
        Long tokenId = analysisResultTokenRepository.findById(excelAnalysisResultId)
                .orElseThrow(NoSuchElementException::new)
                .getId();

        // 접근 토큰 validation
        analysisResultTokenService.validateAndDeleteToken(excelAnalysisResultId, callbackDto.getToken());
        excelAnalysisResultService.handleAnalysisResultCallback(tokenId, callbackDto.getBody());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    private static class CallbackDto {
        private String token;
        private Map<String, String> body;
    }
}
