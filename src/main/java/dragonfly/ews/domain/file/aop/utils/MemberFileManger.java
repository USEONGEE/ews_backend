package dragonfly.ews.domain.file.aop.utils;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.member.domain.Member;
import org.springframework.web.multipart.MultipartFile;

/**
 * UseMemberFileManger 가 붙은 스코프 내에서만 활용이 가능하다.
 *
 * <br/> 자세한 내용은
 * <br/>
 * {@link dragonfly.ews.domain.file.aop.aspect.MemberFileStrategyAspect}
 */
public interface MemberFileManger {
    MemberFile createMemberFile(Member owner, MemberFileCreateDto memberFileCreateDto);

    void beforeUpdateValidate(MemberFile memberFile, MultipartFile target);

    Object findDtoById(Long memberId, Long memberFileId);
}
