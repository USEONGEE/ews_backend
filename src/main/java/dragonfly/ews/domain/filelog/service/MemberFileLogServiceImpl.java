package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberFileLogServiceImpl implements MemberFileLogService {
    private final MemberFileLogRepository memberFileLogRepository;

    /**
     * [단건 조회]
     * @param memberId
     * @param memberFileLogId
     * @return
     */
    @Override
    public MemberFileLog findById(Long memberId, Long memberFileLogId) {
        return memberFileLogRepository.findByIdAuth(memberId, memberFileLogId)
                .orElseThrow(() -> new NoSuchFileException("파일이 존재하지 않습니다."));
    }

    /**
     * [결과 파일 포함 조회]
     * @param memberId
     * @param memberFileLogId
     * @return
     */
    @Override
    public MemberFileLog findByIdContainResults(Long memberId, Long memberFileLogId) {
        memberFileLogRepository.findByIdAuth(memberId, memberFileLogId)
                .orElseThrow(() -> new NoSuchFileException("파일이 존재하지 않습니다."));
        return memberFileLogRepository.findByIdContainResult(memberFileLogId)
                .orElseThrow(() -> new NoSuchFileException("파일이 존재하지 않습니다."));
    }

    /**
     * [전체 조회 - 페이징]
     * @param memberId
     * @param pageable
     * @return
     */
    @Override
    public Page<MemberFileLog> findPaging(Long memberId, Pageable pageable) {
        return memberFileLogRepository.findByOwnerId(memberId, pageable);
    }
}
