package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberFileLogServiceImpl implements MemberFileLogService {
    private MemberFileLogRepository memberFileLogRepository;

    @Override
    public MemberFileLog findMemberFileLog(Long memberId, Long memberFileLogId) {
        return memberFileLogRepository.findByIdAuth(memberId, memberFileLogId)
                .orElseThrow(() -> new NoSuchFileException("파일이 존재하지 않습니다."));
    }
}
