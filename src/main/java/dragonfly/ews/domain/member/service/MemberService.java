package dragonfly.ews.domain.member.service;

import com.google.common.annotations.VisibleForTesting;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberSignUpDto;

public interface MemberService {
    void signUp(MemberSignUpDto memberSignUpDto);

}
