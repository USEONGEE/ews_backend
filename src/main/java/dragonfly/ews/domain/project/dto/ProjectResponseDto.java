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
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDate;
    private List<MemberFileResponseDto> memberFiles;

    public ProjectResponseDto(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.createdDate = project.getCreatedDate();
        this.description = project.getDescription();
        this.memberFiles = project.getMemberFiles()
                .stream()
                .map(MemberFileResponseDto::new)
                .collect(Collectors.toList());
    }

}
