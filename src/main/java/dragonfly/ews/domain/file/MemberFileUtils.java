package dragonfly.ews.domain.file;

import dragonfly.ews.domain.file.exception.ExtensionNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MemberFileUtils {
    public String createSavedFilename(String originalFilename) {
        return UUID.randomUUID().toString() + "." + getFileExt(originalFilename);
    }

    public String getFileExt(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex < 0) {
            throw new ExtensionNotFoundException("파일 확장자를 찾을 수 없습니다. 파일 확장자가 파일 이름에 포함되어야합니다.");
        }
        return fileName.substring(lastDotIndex + 1);
    }
}
