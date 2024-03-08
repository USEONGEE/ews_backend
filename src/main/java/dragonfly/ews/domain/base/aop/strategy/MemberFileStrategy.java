package dragonfly.ews.domain.base.aop.strategy;


import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.member.domain.Member;
import org.springframework.web.multipart.MultipartFile;

public interface MemberFileStrategy extends Strategy {


    /**
     * [파일 확장자에 맞는 MemberFile 생성]
     *
     * @param owner
     * @param memberFileCreateDto
     * @return
     */

    MemberFile createMemberFile(Member owner, MemberFileCreateDto memberFileCreateDto);

    /**
     * [파일 update 전 validation]
     *
     * @param memberFile
     * @param target
     */
    void updateValidate(MemberFile memberFile, MultipartFile target);

    /**
     * [파일 확장자에 따라 화면용 DTO 출력]
     * @param memberId
     * @param memberFileId
     * @return
     */

    Object getMemberFileDtoById(Long memberId, Long memberFileId);

}
