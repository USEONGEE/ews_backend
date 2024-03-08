package dragonfly.ews.domain.file.aop.utils;


import dragonfly.ews.domain.base.aop.strategy.MemberFileStrategy;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service

// TODO UserFileStategy를 통해 전략 주입을 하지 않았으면은 MemberFileManager를 사용하면 안 됨
public class MemberFileManagerImpl implements MemberFileManager, MemberFileManagerConfig {
    private final ThreadLocal<MemberFileStrategy> fileStrategy = new ThreadLocal<>();

    @Override
    public void setFileStrategy(MemberFileStrategy strategy) {
        fileStrategy.set(strategy);
    }

    @Override
    public MemberFileStrategy getFileStrategy() {
        return fileStrategy.get();
    }

    @Override
    public void removeStrategy() {
        fileStrategy.remove();
    }

    @Override
    public MemberFile createMemberFile(Member owner, MemberFileCreateDto memberFileCreateDto) {
        MemberFileStrategy memberFileStrategy = getFileStrategy();
        return memberFileStrategy.createMemberFile(owner, memberFileCreateDto);
    }

    @Override
    public void beforeUpdateValidate(MemberFile memberFile, MultipartFile target) {
        MemberFileStrategy memberFileStrategy = getFileStrategy();
        memberFileStrategy.updateValidate(memberFile, target);
    }

    @Override
    public Object findDtoById(Long memberId, Long memberFileId) {
        MemberFileStrategy memberFileStrategy = getFileStrategy();
        return memberFileStrategy.getMemberFileDtoById(memberId, memberFileId);
    }

}
