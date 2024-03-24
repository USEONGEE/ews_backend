package dragonfly.ews.domain.file.utils;

import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.filelog.dto.ExcelDataDto;
import dragonfly.ews.domain.filelog.exception.CannotResolveFileReadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelFileReaderImpl implements ExcelFileReader {
    private final List<ExcelFileReadProvider> fileReaders;
    private final FileUtils fileUtils;


    @Override
    public ExcelDataDto read(String savedFilename) {
        String fileExt = fileUtils.extractFileExtension(savedFilename);
        FileExtension extension = FileExtension.fromString(fileExt);
        for (ExcelFileReadProvider fileReader : fileReaders) {
            if (fileReader.support(extension)) {
                return fileReader.read(savedFilename);
            }
        }

        throw new CannotResolveFileReadException("파일을 처리할 수 없습니다.");
    }

    @Override
    public List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDto(MultipartFile multipartFile) {
        String fileExt = fileUtils.extractFileExtension(multipartFile.getOriginalFilename());
        FileExtension extension = FileExtension.fromString(fileExt);
        for (ExcelFileReadProvider fileReader : fileReaders) {
            if (fileReader.support(extension)) {
                return fileReader.extractExcelFileColumnCreateDtos(multipartFile);
            }
        }
        throw new CannotResolveFileReadException("파일을 처리할 수 없습니다.");
    }

    @Override
    public List<ExcelFileColumnCreateDto> extractExcelFileColumnCreateDto(String filePath) {
        String fileExt = fileUtils.extractFileExtension(filePath);
        FileExtension extension = FileExtension.fromString(fileExt);
        for (ExcelFileReadProvider fileReader : fileReaders) {
            if (fileReader.support(extension)) {
                return fileReader.extractExcelFileColumnCreateDtos(filePath);
            }
        }
        throw new CannotResolveFileReadException("파일을 처리할 수 없습니다.");
    }
}
