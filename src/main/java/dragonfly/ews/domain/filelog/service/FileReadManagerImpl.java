package dragonfly.ews.domain.filelog.service;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.filelog.controller.ExcelDataDto;
import dragonfly.ews.domain.filelog.exception.CannotResolveFileReadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileReadManagerImpl implements FileReadManager<ExcelDataDto> {
    private final List<FileReader> fileReaders;
    private final FileUtils fileUtils;


    @Override
    public ExcelDataDto resolve(String savedFilename) {
        String fileExt = fileUtils.getFileExt(savedFilename);
        for (FileReader fileReader : fileReaders) {
            if (fileReader.support(fileExt)) {
                return (ExcelDataDto) fileReader.read(savedFilename);
            }
        }

        throw new CannotResolveFileReadException("파일을 처리할 수 없습니다.");
    }
}
