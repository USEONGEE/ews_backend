package dragonfly.ews.domain.filelog.controller;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.domain.file.utils.FileReader;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.dto.ExcelMemberFileLogResponseDto;
import dragonfly.ews.domain.filelog.dto.SingleColumnTransformRequestDto;
import dragonfly.ews.domain.filelog.service.ExcelMemberFileLogService;
import dragonfly.ews.domain.filelog.service.MemberFileLogService;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.result.domain.AnalysisResultToken;
import dragonfly.ews.domain.result.repository.AnalysisResultTokenRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filelog/excel")
public class ExcelMemberFileLogController {
    private final MemberFileLogService memberFileLogService;
    private final ExcelMemberFileLogService excelMemberFileLogService;
    private final FileReader fileReadManager;
    private final AnalysisResultTokenRepository analysisResultTokenRepository;

    @Value("${file.dir}")
    private String fileDir;

    /**
     * [파일로그의 엑셀 데이터를 일부 조회]
     *
     * @param memberFileLogId
     * @param member
     * @return
     */
    @GetMapping("/{memberFileLogId}/data")
    public ResponseEntity<SuccessResponse> fetchUserFileContent(
            @PathVariable(value = "memberFileLogId") Long memberFileLogId,
            @AuthenticationPrincipal(expression = "member") Member member) {
        MemberFileLog memberFileLog = memberFileLogService.findById(member.getId(), memberFileLogId);
        String savedName = memberFileLog.getSavedName();
        return new ResponseEntity<>(SuccessResponse.of(fileReadManager.read(fileDir + savedName)),
                HttpStatus.OK);
    }

    /**
     * [엑셀 데이터의 Column 데이터를 반환]
     *
     * @param memberFileLogId
     * @param member
     * @return
     */
    @GetMapping("/{memberFileLogId}/columns")
    public ResponseEntity<SuccessResponse> fetchFileColumns(
            @PathVariable(value = "memberFileLogId") Long memberFileLogId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        ExcelMemberFileLog excelMemberFileLog = excelMemberFileLogService.findOne(member.getId(), memberFileLogId);
        return new ResponseEntity<>(SuccessResponse.of(new ExcelMemberFileLogResponseDto(excelMemberFileLog)),
                HttpStatus.OK);
    }

    /**
     * [엑셀 데이터 Column Transformation]
     *
     * @param member
     * @param dto
     * @return
     */
    @PostMapping("/columns/single-transform")
    public ResponseEntity<SuccessResponse> singleTransform(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestBody SingleColumnTransformRequestDto dto
    ) {
        return new ResponseEntity<>(SuccessResponse.of(
                excelMemberFileLogService.singleTransform(member.getId(), dto)), HttpStatus.OK);
    }
}
