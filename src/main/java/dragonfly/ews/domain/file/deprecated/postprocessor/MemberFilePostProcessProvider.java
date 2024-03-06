package dragonfly.ews.domain.file.deprecated.postprocessor;

import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.domain.MemberFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일을 저장한 이후에 해당 파일 확장자에 맞는 후속 조치를 취함
 */
public interface MemberFilePostProcessProvider<T> {

    boolean canProcess(FileExtension fileExtension);

    void process(MemberFile target, MultipartFile multipartFile);
}
