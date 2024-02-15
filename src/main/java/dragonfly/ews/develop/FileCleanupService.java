package dragonfly.ews.develop;


import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * [개발 단계에서 사용하는 클래스]
 *
 * 어플리케이션 종료 시점에 디렉토리의 파일을 전부 삭제한다.
 */
@Component
public class FileCleanupService {

    @Value("${file.dir}")
    private String directoryPath;

    @PreDestroy
    public void cleanupFiles() {
        try {
            Files.walk(Paths.get(directoryPath))
                    .map(Path::toFile)
                    .forEach(File::delete); // 각 파일 삭제

            System.out.println("All files in the directory have been deleted.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred while deleting files.");
        }
    }
}