package dragonfly.ews.domain.member.controller;

import com.google.common.annotations.VisibleForTesting;
import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.dto.MemberResponseDto;
import dragonfly.ews.domain.member.dto.MemberSignUpDto;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;


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

    @GetMapping("/my")
    public ResponseEntity<SuccessResponse> mypage(
            @AuthenticationPrincipal(expression = "member") Member member) {
        Member findMember = memberService.findOne(member.getId());
        return new ResponseEntity<>(SuccessResponse.of(MemberResponseDto.of(findMember)), HttpStatus.OK);
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

    @VisibleForTesting
    @GetMapping("/test")
    public Member test(@AuthenticationPrincipal UserDetails userDetails) {
        return memberRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }
}
