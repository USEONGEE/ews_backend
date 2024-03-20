package dragonfly.ews.domain.project.domain;

import dragonfly.ews.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectParticipant {
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

    public ProjectParticipant(Project project, Member member) {
        this.project = project;
        this.member = member;
    }

}
