package dragonfly.ews.domain.result.service;

import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.FileAnalysisResult;

import java.util.List;

public interface FileAnalysisResultService {

    /**
     * [파일 분석을 요청하기 전 FileAnalysisResult 생성]
     * @param memberId
     * @param fileLogId
     * @return
     */
    FileAnalysisResult createFileAnalysisResult(Long memberId, Long fileLogId);

    /**
     * [파일의 분석을 최초로 요청]
     * <p/> 파일 로그를 조회 후, 분석 서버에 분석 요청을 보냄. 분석 요청을 정상적으로 보냈으면
     * FileAnalysisResult 생성 후 사용자에게 파일 분석 로그 반환
     *
     * @param memberId
     * @param fileAnalysisResultId
     * @return dragonfly.ews.domain.result.domain.FileAnalysisResult 의 ID
     */
    Long analysis(Long memberId, Long fileAnalysisResultId);

    /**
     * [분석 진행 상황을 반환]
     * <p/> 사용자가 분석 요청한 분석이 어느 정도 진행이 되었는지를 조회해서 반환
     *
     * @param fileAnalysisResultId
     * @return AnalysisStatus
     */
    AnalysisStatus checkAnalysisStatus(Long memberId, Long fileAnalysisResultId);

    /**
     * [분석 결과를 사용자에게 노출]
     * @param memberId
     * @param fileAnalysisResultId
     * @return FileAnalysisResult
     */
    FileAnalysisResult findByResultId(Long memberId, Long fileAnalysisResultId);

    /**
     * [파일 로그의 분석 결과들을 사용자에게 노출]
     * @param memberId
     * @param fileLogId
     * @return List<FileAnalysisResult>
     */
    List<FileAnalysisResult> findByFileLogId(Long memberId, Long fileLogId);
}
