package dragonfly.ews.domain.result.controller;

import dragonfly.ews.domain.result.service.FileAnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FIleAnalysisResultController {
    private final FileAnalysisResultService fileAnalysisResultService;
}
