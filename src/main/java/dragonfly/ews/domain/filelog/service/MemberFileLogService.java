package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;

import java.util.List;

public interface MemberFileLogService {

    MemberFileLog findMemberFileLog(Long memberId, Long memberFileLogId);
}
