package dragonfly.ews.domain.project.controller;

import dragonfly.ews.common.handler.SuccessResponse;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.domain.Project;
import dragonfly.ews.domain.project.dto.ProjectResponseDto;
import dragonfly.ews.domain.project.service.ParticipateProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [프로젝트 참여자들을 위한 controller]
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/project/participate")
public class ProjectParticipateController {
    private final ParticipateProjectService participateProjectService;

    /**
     * [참여한 프로젝트 전체 조회]
     * @param member
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<SuccessResponse> findAll(
            @AuthenticationPrincipal(expression = "member") Member member) {
        List<Project> projects = participateProjectService.findAll(member.getId());
        List<ProjectResponseDto> result = projects.stream()
                .map(ProjectResponseDto::new)
                .toList();
        return new ResponseEntity<>(SuccessResponse.of(result), HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<SuccessResponse> findOne(
            @AuthenticationPrincipal(expression = "member") Member member,
            @PathVariable(value = "projectId") Long projectId
    ) {
        return new ResponseEntity<>(SuccessResponse.of(new ProjectResponseDto(participateProjectService.findOne(member.getId(), projectId))),
                HttpStatus.OK);
    }

}
