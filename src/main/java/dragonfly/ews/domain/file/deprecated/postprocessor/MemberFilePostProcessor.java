package dragonfly.ews.domain.file.deprecated.postprocessor;

import dragonfly.ews.domain.file.domain.MemberFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberFilePostProcessor {
    private final List<MemberFilePostProcessProvider> processors;

    public void afterSave(MemberFile memberFile, MultipartFile multipartFile) {
        for (MemberFilePostProcessProvider processor : processors) {
            if (processor.canProcess(memberFile.getFileExtension())) {
                processor.process(memberFile, multipartFile);
                return; // 한 개마다 프로세싱을 여러 개 해야하면 리턴을 안 할 수도 ? ?
            }
        }
    }

    public void afterUpdate() {

    }

    // TODO Multipart 없이 하는 것도 구현하기
//    public void process(MemberFile memberFile) {
//
//    }
}
