package dragonfly.ews.domain.filelog.service;

public interface FileReader<T> {
    boolean support(String extension);
    T read(String filePath);
}
