package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.domain.ExcelMemberFile;
import dragonfly.ews.domain.file.domain.MemberFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일을 저장한 이후에 해당 파일 확장자에 맞는 후속 조치를 취함
 */
public interface MemberFilePostProcessor<T> {

    boolean canProcess(String extension);

    void process(MemberFile target, MultipartFile multipartFile);
}
