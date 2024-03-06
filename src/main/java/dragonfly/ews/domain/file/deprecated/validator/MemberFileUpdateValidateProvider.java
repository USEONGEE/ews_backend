package dragonfly.ews.domain.file.deprecated.validator;


import dragonfly.ews.domain.file.domain.MemberFile;
import org.springframework.web.multipart.MultipartFile;

public interface MemberFileUpdateValidateProvider {
    boolean canSupport(String fileExt);

    void validate(MemberFile memberFile, MultipartFile target);

}
