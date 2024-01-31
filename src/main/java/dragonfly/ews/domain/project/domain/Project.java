package dragonfly.ews.domain.project.domain;

import dragonfly.ews.domain.member.domain.Member;
import jakarta.persistence.*;

@Entity
public class Project {

    @Id
    @GeneratedValue
    @Column(name = "project_id")
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;
}
