package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;

public interface FileReadManager<T> {

    T resolve(String savedFilename);
}
