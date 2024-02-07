package dragonfly.ews.domain.member.service;

import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import dragonfly.ews.domain.member.domain.MemberSignUpDto;
import dragonfly.ews.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void signUp(MemberSignUpDto userSignUpDto){
        signUpValidation(userSignUpDto);

        Member member = Member.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getNickname())
                .age(userSignUpDto.getAge())
                .memberRole(MemberRole.ROLE_USER)
                .build();

        member.passwordEncode(passwordEncoder);
        memberRepository.save(member);
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
