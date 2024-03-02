package dragonfly.ews.domain.project.controlelr;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.domain.Project;
import dragonfly.ews.domain.project.dto.ProjectCreateDto;
import dragonfly.ews.domain.project.dto.ProjectResponseDto;
import dragonfly.ews.domain.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<SuccessResponse> createPost(
            @RequestBody ProjectCreateDto projectCreateDto,
            @AuthenticationPrincipal(expression = "member") Member member) {
        projectService.createProject(member.getId(), projectCreateDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<SuccessResponse> findAll(
            @AuthenticationPrincipal(expression = "member") Member member) {
        List<Project> projects = projectService.findAll(member.getId());
        List<ProjectResponseDto> result = projects.stream()
                .map(ProjectResponseDto::new)
                .toList();
        return new ResponseEntity<>(SuccessResponse.of(result), HttpStatus.OK);
    }

    @GetMapping("{projectId}")
    public ResponseEntity<SuccessResponse> findOne(
            @AuthenticationPrincipal(expression = "member") Member member,
            @PathVariable(value = "projectId") Long projectId) {
        return new ResponseEntity<>(SuccessResponse.of(projectService.findOne(member.getId(), projectId)),
                HttpStatus.OK);
    }
}
