package dragonfly.ews.domain.filelog.controller;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.service.FileReader;
import dragonfly.ews.domain.filelog.service.MemberFileLogService;
import dragonfly.ews.domain.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/filelog")
public class MemberFileLogController {
    @Value("${file.dir}")
    private String fileDir;

    private MemberFileLogService memberFileLogService;
    private FileReader fileReader;

    @GetMapping("/fileLogId")
    public ResponseEntity<ExcelDataDto> fetchUserFileContent(
            @PathVariable(value = "fileLogId") Long fileLogId,
            @AuthenticationPrincipal(expression = "member") Member member) {
        MemberFileLog memberFileLog = memberFileLogService.findMemberFileLog(member.getId(), fileLogId);
        String savedName = memberFileLog.getSavedName();
        ExcelDataDto excelDataDto = (ExcelDataDto) fileReader.read(fileDir + savedName);
        return ResponseEntity.ok(excelDataDto);
    }
}
