package dragonfly.ews.domain.result.service;

public interface AnalysisResultProcessor<T, N> {
    void processResult(T result, N id);
}
