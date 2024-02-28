package dragonfly.ews.domain.result.service;

import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.AnalysisResult;

import java.util.List;

public interface AnalysisResultService {

    /**
     * [파일 분석을 요청하기 전 AnalysisResult 생성]
     * @param memberId
     * @param memberFileLogId
     * @return
     */
    AnalysisResult createAnalysisResult(Long memberId, Long memberFileLogId);

    /**
     * [파일의 분석을 최초로 요청]
     * @param memberId
     * @param analysisResultId
     * @return dragonfly.ews.domain.result.domain.AnalysisResult 의 ID
     */
    boolean analysis(Long memberId, Long analysisResultId);

    /**
     * [분석 진행 상황을 반환]
     * <p/> 사용자가 분석 요청한 분석이 어느 정도 진행이 되었는지를 조회해서 반환
     *
     * @param analysisResultId
     * @return AnalysisStatus
     */
    @Deprecated
    AnalysisStatus checkAnalysisStatus(Long memberId, Long analysisResultId);

    /**
     * [분석 결과를 사용자에게 노출]
     * @param memberId
     * @param analysisResultId
     * @return analysisResult
     */
    AnalysisResult findByResultId(Long memberId, Long analysisResultId);

    /**
     * [파일 로그의 분석 결과들을 사용자에게 노출]
     * @param memberId
     * @param fileLogId
     * @return List<AnalysisResult>
     */
    List<AnalysisResult> findByFileLogId(Long memberId, Long fileLogId);

    AnalysisResult findCompletedFileById(Long memberId, Long analysisResultId);
}
