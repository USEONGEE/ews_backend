package dragonfly.ews.domain.file.controller;

import dragonfly.ews.common.security.auth.PrincipalDetails;
import dragonfly.ews.domain.file.service.MemberFileService;
import dragonfly.ews.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class MemberFileController {
    private final MemberFileService memberFileService;

    @PostMapping
    public String addFile(@RequestParam(value = "file") MultipartFile file,
                          @AuthenticationPrincipal(expression = "member") Member member) {
        Long memberId = member.getId();
        memberFileService.saveFile(file, memberId);

        return "파일저장 완료";
    }

    @PostMapping("/{fileId}")
    public String updateFile(@RequestParam(value = "file") MultipartFile file,
                             @PathVariable(value = "fileId") Long fileId,
                             @AuthenticationPrincipal(expression = "member") Member member) {
        Long memberId = member.getId();
        memberFileService.updateFile(file, memberId, fileId);

        return "파일업데이트 완료";
    }
}
