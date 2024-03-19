package dragonfly.ews.domain.member.controller;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberSearchCondDto;
import dragonfly.ews.domain.member.dto.MyPageResponseDto;
import dragonfly.ews.domain.member.dto.MemberSignUpDto;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;


    /**
     * [회원 가입]
     *
     * @param memberSignUpDto
     * @throws RuntimeException 회원 가입에 실패했을 시
     */
    @PostMapping("/sign-up")
    public ResponseEntity<SuccessResponse> signUp(@RequestBody MemberSignUpDto memberSignUpDto) {
        memberService.signUp(memberSignUpDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * [마이 페이지 데이터 반환]
     *
     * @param member
     * @return
     */
    @GetMapping("/my")
    public ResponseEntity<SuccessResponse> mypage(
            @AuthenticationPrincipal(expression = "member") Member member) {
        Member findMember = memberService.findOne(member.getId());
        return new ResponseEntity<>(SuccessResponse.of(MyPageResponseDto.of(findMember)), HttpStatus.OK);
    }


    /**
     * [프로필 이미지 변경]
     *
     * @param member
     * @param multipartFile
     * @return
     */
    @PostMapping("/profile/image")
    public ResponseEntity<SuccessResponse> changeProfileImage(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestBody MultipartFile multipartFile) {
        memberService.changeProfileImage(member.getId(), multipartFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * [회원 조건부 조회]
     */
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse> findByCondition(
            @RequestParam MemberSearchCondDto memberSearchCondDto
    ) {
        List<MyPageResponseDto> list = memberService.findByCond(memberSearchCondDto)
                .stream()
                .map(MyPageResponseDto::of)
                .toList();
        return new ResponseEntity<>(SuccessResponse.of(list), HttpStatus.OK);
    }
}
