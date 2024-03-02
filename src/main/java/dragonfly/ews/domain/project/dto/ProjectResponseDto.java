package dragonfly.ews.domain.project.dto;

import dragonfly.ews.domain.file.dto.MemberFileResponseDto;
import dragonfly.ews.domain.project.domain.Project;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProjectResponseDto {
    private Long projectId;
    private String name;
    private String description;
    private List<MemberFileResponseDto> memberFiles;

    public ProjectResponseDto(Project project) {
        this.projectId = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.memberFiles = project.getMemberFiles()
                .stream()
                .map(MemberFileResponseDto::new)
                .collect(Collectors.toList());
    }

}
