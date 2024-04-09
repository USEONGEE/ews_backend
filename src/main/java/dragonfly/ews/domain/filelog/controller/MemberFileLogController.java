package dragonfly.ews.domain.filelog.controller;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.dto.ExcelDataDto;
import dragonfly.ews.domain.filelog.dto.MemberFileLogContainResultsResponseDto;
import dragonfly.ews.domain.filelog.dto.MemberFileLogResponseDto;
import dragonfly.ews.domain.file.utils.ExcelFileReader;
import dragonfly.ews.domain.filelog.service.MemberFileLogService;
import dragonfly.ews.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/filelog")
public class MemberFileLogController {
    private final MemberFileLogService memberFileLogService;

    /**
     * [파일로그 세부 조회]
     *
     * @param memberFileLogId 파일로그 아이디
     * @param member         로그인한 사용자
     * @return
     */
    @GetMapping("/{memberFileLogId}")
    public ResponseEntity<SuccessResponse> findByMemberFileLogId(
            @PathVariable(value = "memberFileLogId") Long memberFileLogId,
            @AuthenticationPrincipal(expression = "member") Member member) {
        MemberFileLog memberFileLog = memberFileLogService.findByIdContainResults(member.getId(), memberFileLogId);

        return new ResponseEntity<>(SuccessResponse.of(new MemberFileLogContainResultsResponseDto(memberFileLog)),
                HttpStatus.OK);
    }

    /**
     * [파일 로그 페이징으로 조회]
     * @param member 로그인한 사용자
     * @param pageable 페이징 정보
     * @return
     */
    @GetMapping
    public ResponseEntity<SuccessResponse> findPaging(
            @AuthenticationPrincipal(expression = "member") Member member,
            Pageable pageable) {
        Page<MemberFileLog> paging = memberFileLogService.findPaging(member.getId(), pageable);
        Page<MemberFileLogResponseDto> result = paging.map(MemberFileLogResponseDto::new);
        return new ResponseEntity<>(SuccessResponse.of(result), HttpStatus.OK);
    }
}
