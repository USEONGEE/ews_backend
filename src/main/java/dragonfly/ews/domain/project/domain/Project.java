package dragonfly.ews.domain.project.domain;

import dragonfly.ews.domain.base.BaseEntity;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.exception.CannotChangeProjectOwnerException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
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
    private List<ProjectFile> projectFiles = new ArrayList<>();

    public Project(String projectName, Member owner) {
        this.name = projectName;
        this.owner = owner;
        owner.getMyProjects().add(this);
    }

    // ==편의 메소드==
    public void addMemberFile(@NotNull MemberFile memberFile) {
        ProjectFile projectFile = ProjectFile.createProjectFile(this, memberFile);
        getProjectFiles().add(projectFile);
    }

}
