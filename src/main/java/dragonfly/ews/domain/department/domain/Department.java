package dragonfly.ews.domain.department.domain;

import dragonfly.ews.domain.university.domian.University;
import jakarta.persistence.*;

@Entity
public class Department {
    @Id
    @GeneratedValue
    @Column(name = "department_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;

    private String name;
}
