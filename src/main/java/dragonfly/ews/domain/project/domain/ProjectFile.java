package dragonfly.ews.domain.project.domain;

import dragonfly.ews.domain.file.domain.MemberFile;
import jakarta.persistence.*;

@Entity
public class ProjectFile {
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
}
