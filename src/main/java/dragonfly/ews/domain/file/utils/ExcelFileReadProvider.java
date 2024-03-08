package dragonfly.ews.domain.file.utils;

import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.filelog.dto.ExcelDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelFileReadProvider {
    boolean support(FileExtension fileExtension);
    ExcelDataDto read(String filePath);

    List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDtos(MultipartFile multipartFile);
}
