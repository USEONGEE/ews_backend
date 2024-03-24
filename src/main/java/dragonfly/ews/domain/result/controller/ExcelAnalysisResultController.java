package dragonfly.ews.domain.result.controller;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.develop.aop.LogMethodParams;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.result.domain.ExcelAnalysisResult;
import dragonfly.ews.domain.result.dto.ExcelAnalysisResultResponseDto;
import dragonfly.ews.domain.result.service.ExcelAnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis/excel")
public class ExcelAnalysisResultController {
    private final ExcelAnalysisResultService excelAnalysisResultService;

    /**
     * [ExcelAnalysisResult 단건 조회]
     * @param member
     * @param excelAnalysisResultId
     * @return
     */
    @GetMapping("/{excelAnalysisResultId}")
    @LogMethodParams
    public ResponseEntity<SuccessResponse> findOne(
            @AuthenticationPrincipal(expression = "member") Member member,
            @PathVariable(value = "excelAnalysisResultId") Long excelAnalysisResultId) {
        ExcelAnalysisResult find = excelAnalysisResultService.findOne(member.getId(), excelAnalysisResultId);
        return new ResponseEntity<>(SuccessResponse.of(ExcelAnalysisResultResponseDto.of(find)), HttpStatus.OK);
    }


}
