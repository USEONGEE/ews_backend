package dragonfly.ews.domain.result.service;


public interface AnalysisResultProcessor<T, N> {
    /**
     * 분석 요청 이후, 서버에게 받은 응답값 대한 행동
     * @param result
     * @param id
     */
    void processResult(T result, N id);
}
