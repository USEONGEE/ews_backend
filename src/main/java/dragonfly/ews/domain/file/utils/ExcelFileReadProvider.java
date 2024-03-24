package dragonfly.ews.domain.file.utils;

import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.filelog.dto.ExcelDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * [ExcelFileReader에서 사용됨]
 * <br/> ExcelFileReader에서 확장자에 맞는 엑셀 파일을 읽기 위해서 사용됨
 */
public interface ExcelFileReadProvider {
    boolean support(FileExtension fileExtension);
    ExcelDataDto read(String filePath);

    List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDtos(MultipartFile multipartFile);

    List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDtos(String filePath);
}
