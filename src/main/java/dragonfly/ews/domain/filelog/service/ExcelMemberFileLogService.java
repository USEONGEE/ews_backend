package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.controller.ExcelMemberFileLogController;
import dragonfly.ews.domain.filelog.domain.ExcelFileColumn;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import dragonfly.ews.domain.filelog.dto.SingleColumnTransformRequestDto;
import dragonfly.ews.domain.filelog.exception.NoSuchMemberFileLogException;
import dragonfly.ews.domain.filelog.repository.ExcelMemberFileLogRepository;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.filelog.util.SingleColumnTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExcelMemberFileLogService {
    private final MemberFileLogRepository memberFileLogRepository;
    private final ExcelMemberFileLogRepository excelMemberFileLogRepository;
    private final List<SingleColumnTransformer> singleColumnTransformers;
    private final MemberFileRepository memberFileRepository;

    @Value("${file.dir}")
    private String fileDir;

    public ExcelMemberFileLog findOne(Long memberId, Long excelMemberFileLogId) {
        memberFileLogRepository.findByIdAuth(memberId, excelMemberFileLogId)
                .orElseThrow(NoSuchMemberFileLogException::new);
        ExcelMemberFileLog excelMemberFileLog = excelMemberFileLogRepository.findByIdContainColumn(excelMemberFileLogId)
                .orElseThrow(NoSuchMemberFileLogException::new);
        return excelMemberFileLog;
    }

    /**
     * [엑셀 파일 Transformation]
     *
     * @param memberId
     * @param dto
     * @return
     */
    public boolean singleTransform(Long memberId, SingleColumnTransformRequestDto dto) {
        FileExtension extension = memberFileRepository.findExtensionByMemberFileLogId(dto.getMemberFileLogId())
                .orElseThrow(NoSuchMemberFileLogException::new);

        try {
            for (SingleColumnTransformer singleColumnTransformer : singleColumnTransformers) {
                if (singleColumnTransformer.canSupport(extension)) {
                    singleColumnTransformer.transform(dto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    /**
     * [분석 서버에서 excel file 의 column을 validation 하고 excelColumn을 추가하는 과정]
     * @param excelMemberFileLogId
     * @param dtos
     * @return
     */
    @Transactional
    public boolean updateColumn(Long excelMemberFileLogId, List<ExcelFileColumnCreateDto> dtos) {
        ExcelMemberFileLog excelMemberFileLog = excelMemberFileLogRepository.findById(excelMemberFileLogId)
                .orElseThrow(NoSuchMemberFileLogException::new);

        for (ExcelFileColumnCreateDto dto : dtos) {
            ExcelFileColumn excelFileColumn = new ExcelFileColumn(dto);
            excelMemberFileLog.addColumn(excelFileColumn);
        }

        return true;
    }

}
