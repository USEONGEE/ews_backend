package dragonfly.ews.domain.file.controller;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileContainLogsResponseDto;
import dragonfly.ews.domain.file.dto.MemberFileResponseDto;
import dragonfly.ews.domain.file.service.MemberFileService;
import dragonfly.ews.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class MemberFileController {
    private final MemberFileService memberFileService;

    /**
     * [파일 추가]
     *
     * @param file
     * @param member
     * @return
     */
    @PostMapping
    public ResponseEntity<SuccessResponse> addFile(@RequestParam(value = "file") MultipartFile file,
                                                   @AuthenticationPrincipal(expression = "member") Member member) {
        return new ResponseEntity<>(SuccessResponse.of(memberFileService.saveFile(file, member.getId())),
                HttpStatus.CREATED);
    }

    /**
     * [파일 업데이트]
     * <br/> 파일 로그 추가
     *
     * @param file
     * @param memberFileId
     * @param member
     * @return
     */
    @PostMapping("/{memberFileId}")
    public ResponseEntity<SuccessResponse> updateFile(@RequestParam(value = "file") MultipartFile file,
                                                      @PathVariable(value = "memberFileId") Long memberFileId,
                                                      @AuthenticationPrincipal(expression = "member") Member member) {
        return new ResponseEntity<>(SuccessResponse.of(memberFileService.updateFile(file, member.getId(),
                memberFileId)), HttpStatus.CREATED);
    }

    /**
     * [파일 세부 조회]
     * <br/> 하나의 논리적 파일에 속한 파일 로그 조회
     *
     * @param memberFileId
     * @param member
     * @return
     */
    @GetMapping("/{memberFileId}")
    public ResponseEntity<SuccessResponse> findFile(@PathVariable(value = "memberFileId") Long memberFileId,
                                                    @AuthenticationPrincipal(expression = "member") Member member) {
        MemberFile memberFile = memberFileService.findByIdContainLogs(member.getId(), memberFileId);
        return new ResponseEntity<>(SuccessResponse.of(new MemberFileContainLogsResponseDto(memberFile)), HttpStatus.OK);
    }

    /**
     * [파일 전체 조회]
     * <br/> 회원이 가지고 있는 논리 파일 전체 조회
     * @param member
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<SuccessResponse> findAll(@AuthenticationPrincipal(expression = "member") Member member) {
        List<MemberFile> memberFiles = memberFileService.findAll(member.getId());
        List<MemberFileResponseDto> dtos = memberFiles.stream()
                .map(MemberFileResponseDto::new)
                .toList();
        return new ResponseEntity<>(SuccessResponse.of(dtos), HttpStatus.OK);
    }
}
