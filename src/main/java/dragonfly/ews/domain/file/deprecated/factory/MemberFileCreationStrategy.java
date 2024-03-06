package dragonfly.ews.domain.file.deprecated.factory;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.member.domain.Member;

public interface MemberFileCreationStrategy {
    boolean supports(String fileType);
    MemberFile create(Member owner, String submittedFileName, String originalFilename, String savedFilename);
}