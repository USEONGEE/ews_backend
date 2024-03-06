package dragonfly.ews.domain.file.deprecated.factory;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.exception.NoFileNameException;
import dragonfly.ews.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberFileFactory {
    private final List<MemberFileCreationStrategy> memberFileCreationStrategies;
    private final FileUtils memberFileUtils;

    public MemberFile create(Member member, MemberFileCreateDto memberFileCreateDto) {
        String originalFilename = memberFileCreateDto.getFile().getOriginalFilename();
        hasName(originalFilename);
        String savedFilename = memberFileUtils.createSavedFilename(originalFilename);
        String fileExt = memberFileUtils.getFileExt(originalFilename);
        MemberFile memberFile = null;

        for (MemberFileCreationStrategy memberFileCreationStrategy : memberFileCreationStrategies) {
            if (memberFileCreationStrategy.supports(fileExt)) {
                memberFile = memberFileCreationStrategy.create(member,
                        memberFileCreateDto.getFileName(),
                        originalFilename,
                        savedFilename);
                break;
            }
        }

        memberFile =  new MemberFile(member,
                memberFileCreateDto.getFileName(),
                originalFilename,
                savedFilename);

        memberFileUtils.storeFile(memberFileCreateDto.getFile(), savedFilename);
        return memberFile;
    }

    private void hasName(String fileName) {
        if (fileName.isEmpty()) {
            throw new NoFileNameException("사용자가 제공한 파일에 이름이 없습니다.");
        }
    }
}
