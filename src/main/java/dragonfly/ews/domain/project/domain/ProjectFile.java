package dragonfly.ews.domain.project.domain;

import dragonfly.ews.domain.base.BaseEntity;
import dragonfly.ews.domain.file.domain.MemberFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Deprecated
public class ProjectFile extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "project_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private MemberFile memberFile;

    // ==생성 메소드==
    public static ProjectFile createProjectFile(Project project, MemberFile memberFile) {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setMemberFile(memberFile);
        projectFile.setProject(project);
        return projectFile;
    }
}
