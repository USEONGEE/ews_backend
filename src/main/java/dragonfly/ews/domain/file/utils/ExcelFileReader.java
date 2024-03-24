package dragonfly.ews.domain.file.utils;

import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.filelog.dto.ExcelDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelFileReader extends FileReader {

    @Override
    ExcelDataDto read(String savedFilename);

    List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDto(MultipartFile multipartFile);

    List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDto(String filePath);
}
