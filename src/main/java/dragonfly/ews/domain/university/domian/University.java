package dragonfly.ews.domain.university.domian;

import dragonfly.ews.domain.department.domain.Department;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class University {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String createdDate;

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL)
    private List<Department> departments = new ArrayList<>();


}
