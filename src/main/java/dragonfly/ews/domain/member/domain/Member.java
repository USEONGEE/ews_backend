package dragonfly.ews.domain.member.domain;

import dragonfly.ews.domain.base.BaseTimeEntity;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.member.exception.CannotChangePasswordException;
import dragonfly.ews.domain.project.domain.ProjectParticipant;
import dragonfly.ews.domain.project.domain.Project;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    @Column(unique = true, updatable = false)
    private String email;
    private String password;
    private Integer age;
    private String nickname;
    private String refreshToken;
    private String provider;
    private String providerId;
    private String profileImage = "DefaultUserImage.jpg";

    @Enumerated(value = EnumType.STRING)
    private MemberRole memberRole = MemberRole.ROLE_USER;

    @OneToMany(mappedBy = "owner")
    private List<MemberFile> memberFiles = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Project> myProjects = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ProjectParticipant> projectParticipants = new ArrayList<>();


    public Member(String email, Integer age, MemberRole memberRole) {
        this.email = email;
        this.age = age;
        this.memberRole = memberRole;
        log.trace("[Member] 새로운 맴버 생성");
    }

    /**
     * [refreshToken 업데이트]
     */
    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        log.trace("[Member] 리프래시 토큰 업데이트, 토큰 값={}", newRefreshToken);
    }

    /**
     * [비밀번호 암호화]
     * @exception IllegalArgumentException 같은 비밀번호로 변경할 수 없다.
     */
    public void changePassword(PasswordEncoder passwordEncoder, String newPassword) {
        if (passwordEncoder.matches(newPassword ,getPassword())) {
            throw new IllegalArgumentException("같은 비밀번호로 변경할 수 없습니다.");
        }

        setPassword(passwordEncoder.encode(newPassword));
        log.info("[Member] 비밀번호 암호화, 비밀번호={}", getPassword());
    }

    public void changeNickname(String newNickname) {
        setNickname(newNickname);
    }

    public void changeMemberRole(MemberRole memberRole) {
        setMemberRole(memberRole);
    }

    public void addProviderAndId(String provider, String providerId) {
        setProvider(provider);
        setProviderId(providerId);
    }

    public void changeImageName(String imageName) {
        setProfileImage(imageName);
    }
}
