package dragonfly.ews.domain.file.validator;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.exception.ExtensionMismatchException;
import dragonfly.ews.domain.file.exception.FileNotInProjectException;
import dragonfly.ews.domain.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ExcelMemberFileUpdateValidateProvider implements MemberFileUpdateValidateProvider {
    private final FileUtils memberFileUtils;

    @Override
    public boolean canSupport(String fileExt) {
        return fileExt.equalsIgnoreCase("csv") || fileExt.equalsIgnoreCase("xlsx")
                || fileExt.equalsIgnoreCase("xls");
    }

    @Override
    public void validate(MemberFile memberFile, MultipartFile target) {
        hasProject(memberFile);
        // 파일 확장자가 같은지
        String fileExt = memberFileUtils.getFileExt(target.getOriginalFilename());
        checkFileExtension(memberFile, fileExt);
        // TODO 파일 column이 같은지 + 데이터 타입이 같은지
    }

    private void hasProject(MemberFile memberFile) {
        Project project = memberFile.getProject();
        if (project == null) {
            throw new FileNotInProjectException("파일이 프로젝트에 포함되지 않았습니다.");
        }
    }

    private void checkFileExtension(MemberFile memberFile, String originalFilename) {
        if (!memberFile.isSameExt(originalFilename)) {
            throw new ExtensionMismatchException("파일 확장자가 같아야합니다.");
        }
    }
}
