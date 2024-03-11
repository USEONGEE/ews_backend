package dragonfly.ews.domain.file.utils;

import dragonfly.ews.domain.file.exception.CannotReadFileException;
import dragonfly.ews.domain.file.exception.CannotSaveFileException;
import dragonfly.ews.domain.file.exception.ExtensionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Component
@Slf4j
public class FileUtils {
    @Value("${file.dir}")
    private String fileDir;

    public String createSavedFilename(String originalFilename) {
        return UUID.randomUUID().toString() + "." + extractFileExtension(originalFilename);
    }

    public String extractFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex < 0) {
            throw new ExtensionNotFoundException("파일 확장자를 찾을 수 없습니다. 파일 확장자가 파일 이름에 포함되어야합니다.");
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * [파일을 실제 물리적으로 저장하는 로직]
     * <p>
     * 파라미터로 파일의 경로를 제공하지 않아도 됨
     *
     * @param file
     * @param savedFilename
     * @throws CannotSaveFileException
     */
    public void storeFile(MultipartFile file, String savedFilename) {
        try {
            file.transferTo(new File(fileDir + savedFilename));
        } catch (IOException e) {
            log.error("파일 저장에 문제가 발생했습니다.");
            throw new CannotSaveFileException(e);
        }
    }

    public byte[] readFileContentByPath(String path) {
        File file = new File(path);
        byte[] fileContent = null;
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new CannotReadFileException(e);
        }

        return fileContent;
    }
}
