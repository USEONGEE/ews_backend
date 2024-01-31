package dragonfly.ews.domain.member.domain;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.domain.ParticipateProject;
import dragonfly.ews.domain.project.domain.Project;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String email;
    private int age;

    @Enumerated(value = EnumType.STRING)
    private MemberRole memberRole;

    @OneToMany(mappedBy = "owner")
    private List<MemberFile> memberFiles = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Project> myProjects = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<ParticipateProject> participateProjects = new ArrayList<>();

}
