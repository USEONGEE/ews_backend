package dragonfly.ews.domain.filelog.service;

public interface FileReadManager<T> {

    T resolve(String savedFilename);
}
