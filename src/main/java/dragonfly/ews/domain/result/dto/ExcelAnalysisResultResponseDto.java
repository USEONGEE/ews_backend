package dragonfly.ews.domain.result.dto;

import dragonfly.ews.domain.result.domain.AnalysisResultFile;
import dragonfly.ews.domain.result.domain.ExcelAnalysisResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class ExcelAnalysisResultResponseDto {
    private Long id;
    private String description;
    private LocalDateTime createdTime;
    private List<AnalysisResultFileResponseDto> resultFiles = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @Builder
    private static class AnalysisResultFileResponseDto {
        private Long id;
        private String filename;

        private static AnalysisResultFileResponseDto of(AnalysisResultFile analysisResultFile) {
            return AnalysisResultFileResponseDto.builder()
                    .id(analysisResultFile.getId())
                    .filename(analysisResultFile.getFilename())
                    .build();
        }
    }

    public static ExcelAnalysisResultResponseDto of(ExcelAnalysisResult excelAnalysisResult) {
        List<AnalysisResultFile> analysisResultFiles = excelAnalysisResult.getAnalysisResultFiles();
        List<AnalysisResultFileResponseDto> collect = analysisResultFiles.stream()
                .map(AnalysisResultFileResponseDto::of)
                .collect(Collectors.toList());
        return ExcelAnalysisResultResponseDto.builder()
                .id(excelAnalysisResult.getId())
                .createdTime(excelAnalysisResult.getCreatedDate())
                .resultFiles(collect)
                .build();
    }
}
