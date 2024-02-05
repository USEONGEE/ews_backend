package dragonfly.ews.domain.member.domain;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.domain.ParticipateProject;
import dragonfly.ews.domain.project.domain.Project;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@Builder
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String password;
    private int age;
    private String nickname;
    private String refreshToken;

    @Enumerated(value = EnumType.STRING)
    private MemberRole memberRole;

    @OneToMany(mappedBy = "owner")
    private List<MemberFile> memberFiles = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Project> myProjects = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<ParticipateProject> participateProjects = new ArrayList<>();

    public Member(String email, int age, MemberRole memberRole) {
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
     */
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
