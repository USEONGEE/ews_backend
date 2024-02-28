package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;

public interface MemberFileLogService {

    MemberFileLog findById(Long memberId, Long memberFileLogId);

    MemberFileLog findByIdContainResults(Long memberId, Long memberFileLogId);
}
