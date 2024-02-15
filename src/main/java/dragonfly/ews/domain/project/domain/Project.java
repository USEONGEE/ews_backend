package dragonfly.ews.domain.project.domain;

import dragonfly.ews.domain.base.BaseEntity;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.exception.CannotChangeProjectOwnerException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "project_id")
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<MemberFile> memberFiles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<ParticipateProject> participateProjects = new ArrayList<>();

    public Project(String projectName, Member owner) {
        this.name = projectName;
        this.owner = owner;
        owner.getMyProjects().add(this);
    }

    // ===편의 메소드===
    public void addParticipants(@NotNull Member member) {
        ParticipateProject participateProject = new ParticipateProject(this, member);
        getParticipateProjects().add(participateProject);
    }

    public void addMemberFile(@NotNull MemberFile memberFile) {
        memberFile.addProject(this);
        getMemberFiles().add(memberFile);

    }
}
