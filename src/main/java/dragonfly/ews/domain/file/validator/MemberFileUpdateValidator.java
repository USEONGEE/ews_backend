package dragonfly.ews.domain.file.validator;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.domain.MemberFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberFileUpdateValidator {
    private final List<MemberFileUpdateValidateProvider> validateProviders;
    private final FileUtils fileUtils;

    public void validate(MemberFile memberFile, MultipartFile multipartFile) {
        String fileExt = fileUtils.getFileExt(multipartFile.getOriginalFilename());
        for (MemberFileUpdateValidateProvider validateProvider : validateProviders) {
            if (validateProvider.canSupport(fileExt)) {
                validateProvider.validate(memberFile, multipartFile);
                return;
            }
        }
    }
}
