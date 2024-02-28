package dragonfly.ews.domain.member.service;

import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import dragonfly.ews.domain.member.domain.MemberSignUpDto;
import dragonfly.ews.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void signUp(MemberSignUpDto userSignUpDto){
        signUpValidation(userSignUpDto);

        Member newMember = new Member(userSignUpDto.getEmail(), userSignUpDto.getAge(), MemberRole.ROLE_USER);
        newMember.changePassword(passwordEncoder, userSignUpDto.getPassword());
        newMember.changeNickname(userSignUpDto.getNickname());

        memberRepository.save(newMember);
    }

    private void signUpValidation(MemberSignUpDto userSignUpDto) {
        if (memberRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        if (memberRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }
    }
}
