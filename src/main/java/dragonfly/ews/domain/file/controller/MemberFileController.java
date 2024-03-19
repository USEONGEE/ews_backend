package dragonfly.ews.domain.file.controller;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.domain.file.aop.utils.MemberFileManager;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileResponseDto;
import dragonfly.ews.domain.file.dto.MemberFileUpdateDto;
import dragonfly.ews.domain.file.service.MemberFileService;
import dragonfly.ews.domain.member.domain.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class MemberFileController {
    private final MemberFileService memberFileService;

    /**
     * [파일 추가]
     * <p/> - 파일의 확장자(ex. csv, xlsx, pdf .. )에 따라 다른 절차를 통해 저장됨
     * <br/>- {@link MemberFileManager}를 참조
     * @param member
     * @param member
     * @return
     */
    @PostMapping
    public ResponseEntity<SuccessResponse> addFile(@ModelAttribute MemberFileCreateDto memberFileCreateDto,
                                                   @AuthenticationPrincipal(expression = "member") Member member) {
        return new ResponseEntity<>(SuccessResponse.of(memberFileService.saveFile(member.getId(),
                memberFileCreateDto)),
                HttpStatus.CREATED);
    }

    /**
     * [파일 업데이트]
     * <p/> - 파일 로그 추가
     * <br/> - 파일의 확장자(ex. csv, xlsx, pdf .. )에 따라 다른 절차를 통해 저장됨
     * <br/>- {@link MemberFileManager}를 참조
     * @param member
     * @return
     */
    @PostMapping("/update")
    public ResponseEntity<SuccessResponse> updateFile(
            @ModelAttribute @Valid MemberFileUpdateDto memberFileUpdateDto,
            @AuthenticationPrincipal(expression = "member") Member member) {
        return new ResponseEntity<>(SuccessResponse.of(memberFileService.updateFile(member.getId(),
                memberFileUpdateDto)), HttpStatus.OK);
    }

    /**
     * [파일 세부 조회]
     * <p/> - 하나의 논리적 파일 조회
     * <br/>- memberFileLogs를 포함
     * <br/>- 파일의 확장자(ex. csv, xlsx, pdf .. )에 따라서 다른 값이 추가될 수 있음
     * <br/>- {@link MemberFileManager}를 참조
     *
     * @param memberFileId
     * @param member
     * @return
     */
    @GetMapping("/{memberFileId}")
    public ResponseEntity<SuccessResponse> findFile(@PathVariable(value = "memberFileId") Long memberFileId,
                                                    @AuthenticationPrincipal(expression = "member") Member member) {
        Object data = memberFileService.findByIdContainLogs(member.getId(), memberFileId);
        return new ResponseEntity<>(SuccessResponse.of(data), HttpStatus.OK);
    }

    /**
     * [파일 전체 조회]
     * <p/> 회원이 가지고 있는 논리 파일 전체 조회
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<SuccessResponse> findAll(
            @AuthenticationPrincipal(expression = "member") Member member,
            Pageable pageable) {
        Page<MemberFile> paging = memberFileService.findPaging(member.getId(), pageable);
        Page<MemberFileResponseDto> dtos = paging.map(MemberFileResponseDto::of);
        return new ResponseEntity<>(SuccessResponse.of(dtos), HttpStatus.OK);
    }
}
