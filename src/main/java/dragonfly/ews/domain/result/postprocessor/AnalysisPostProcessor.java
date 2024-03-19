package dragonfly.ews.domain.result.postprocessor;


public interface AnalysisPostProcessor<T, N> {
    /**
     * 분석 요청 이후, 서버에게 받은 응답값 대한 행동
     * @param result
     * @param id
     */
    void success(T result, N id);

    void fail(Exception e, N id);
}
