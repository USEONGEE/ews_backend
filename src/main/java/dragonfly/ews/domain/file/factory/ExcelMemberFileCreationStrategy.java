package dragonfly.ews.domain.file.factory;

import dragonfly.ews.domain.file.domain.ExcelMemberFile;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class ExcelMemberFileCreationStrategy implements MemberFileCreationStrategy {
    @Override
    public boolean supports(String fileType) {
        return "xls".equalsIgnoreCase(fileType) || "xlsx".equalsIgnoreCase(fileType) || "csv".equalsIgnoreCase(fileType);
    }

    @Override
    public MemberFile create(Member owner, String submittedFileName, String originalFilename, String savedFilename) {
        // ExcelMemberFile 생성 로직
        return new ExcelMemberFile(owner, submittedFileName, originalFilename, savedFilename);
    }
}
