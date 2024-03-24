package dragonfly.ews.domain.member.service;

import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import dragonfly.ews.domain.member.domain.MemberSearchCondDto;
import dragonfly.ews.domain.member.dto.MemberSignUpDto;
import dragonfly.ews.domain.member.exception.NoSuchMemberException;
import dragonfly.ews.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUtils fileUtils;

    @Value("${file.dir}")
    private String fileDir;

    @Transactional
    public void signUp(MemberSignUpDto userSignUpDto) {
        signUpValidation(userSignUpDto);

        Member newMember = new Member(userSignUpDto.getEmail(), userSignUpDto.getAge(), MemberRole.ROLE_USER);
        newMember.changePassword(passwordEncoder, userSignUpDto.getPassword());
        newMember.changeNickname(userSignUpDto.getNickname());

        memberRepository.save(newMember);
    }

    @Transactional
    public boolean changeProfileImage(Long memberId, MultipartFile multipartFile) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchMemberException::new);

        // 이미지 파일명 저장
        String savedFilename = fileUtils.createSavedFilename(multipartFile.getOriginalFilename());
        member.changeImageName(savedFilename);

        // 이미지 저장
        fileUtils.storeFile(multipartFile, savedFilename);

        return true;
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(NoSuchMemberException::new);
    }

    public List<Member> findByCond(MemberSearchCondDto memberSearchCondDto) {
        return memberRepository.findByEmailContaining(memberSearchCondDto.getEmail());
    }

    private void signUpValidation(MemberSignUpDto userSignUpDto) {
        if (memberRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 메일입니다.");
        }
    }
}
