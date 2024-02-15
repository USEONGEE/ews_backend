package dragonfly.ews.domain.file.controller;

import com.google.common.annotations.VisibleForTesting;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.domain.MemberFileResponseDto;
import dragonfly.ews.domain.file.service.MemberFileService;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{fileId}")
    public ResponseEntity<List<MemberFileResponseDto>> findFile(@PathVariable(value = "fileId") Long fileId,
                                                          @AuthenticationPrincipal(expression = "member") Member member) {
        List<MemberFileLog> memberFileById = memberFileService.findMemberFileDetails(member.getId(), fileId);
        List<MemberFileResponseDto> list = memberFileById.stream()
                .map(MemberFileResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }


    @VisibleForTesting
    @GetMapping("/{fileId}/logs/test")
    public String findFilesLogTest(@PathVariable(value = "fileId") Long fileId,
                                   @AuthenticationPrincipal(expression = "member") Member member) {
        Long memberId = member.getId();
        List<MemberFileLog> memberFileById = memberFileService.findMemberFileDetails(memberId, fileId);

        return String.valueOf(memberFileById.size());
    }

    @VisibleForTesting
    @GetMapping("/all")
    public String findAllTest(@AuthenticationPrincipal(expression = "member") Member member) {
        Long memberId = member.getId();
        List<MemberFile> all = memberFileService.findAll(memberId);

        return String.valueOf(all.size());
    }
}
