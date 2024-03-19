package dragonfly.ews.domain.member.service;

import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.dto.MemberSignUpDto;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void signUp(MemberSignUpDto memberSignUpDto);

    boolean changeProfileImage(Long memberId, MultipartFile multipartFile);

    Member findOne(Long memberId);
}
