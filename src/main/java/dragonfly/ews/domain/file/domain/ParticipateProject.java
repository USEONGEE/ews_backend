package dragonfly.ews.domain.file.domain;

import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.domain.Project;
import jakarta.persistence.*;

@Entity
public class ParticipateProject {
    @Id
    @GeneratedValue
    @Column(name = "participate_project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


}
