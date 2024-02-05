package dragonfly.ews.domain.university.domian;

import dragonfly.ews.domain.department.domain.Department;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class University {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL)
    private List<Department> departments = new ArrayList<>();


}
