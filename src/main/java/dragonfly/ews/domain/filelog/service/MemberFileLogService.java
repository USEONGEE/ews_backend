package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberFileLogService {

    MemberFileLog findById(Long memberId, Long memberFileLogId);

    MemberFileLog findByIdContainResults(Long memberId, Long memberFileLogId);

    Page<MemberFileLog> findPaging(Long memberId, Pageable pageable);
}
