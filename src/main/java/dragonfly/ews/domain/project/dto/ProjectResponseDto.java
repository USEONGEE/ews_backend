package dragonfly.ews.domain.project.dto;

import dragonfly.ews.domain.file.dto.MemberFileResponseDto;
import dragonfly.ews.domain.project.domain.Project;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProjectResponseDto {
    private Long projectId;
    private String name;
    private String description;
    private LocalDateTime createdData;
    private List<MemberFileResponseDto> memberFiles;

    public ProjectResponseDto(Project project) {
        this.projectId = project.getId();
        this.name = project.getName();
        this.createdData = project.getCreatedDate();
        this.description = project.getDescription();
//        this.memberFiles = project.getMemberFiles()
//                .stream()
//                .map(MemberFileResponseDto::new)
//                .collect(Collectors.toList());
    }

}
