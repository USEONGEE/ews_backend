package dragonfly.ews.domain.filelog.service;

public interface FileReader<T> {
    T read(String filePath);
}
