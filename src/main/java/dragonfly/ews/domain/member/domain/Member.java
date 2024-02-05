package dragonfly.ews.domain.member.domain;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.domain.ParticipateProject;
import dragonfly.ews.domain.project.domain.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Slf4j
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
}
