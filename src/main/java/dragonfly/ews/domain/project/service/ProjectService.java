package dragonfly.ews.domain.project.service;

import dragonfly.ews.domain.project.controlelr.ParticipantDto;
import dragonfly.ews.domain.project.controlelr.ProjectCreateDto;
import dragonfly.ews.domain.project.domain.Project;

public interface ProjectService {

    /**
     * [프로젝트 생성]
     *
     * @return
     */
    Project createProject(Long ownerId, ProjectCreateDto projectCreateDto);


    /**
     * [특정 프로젝트에 맴버 추가하기]
     */

    Project addParticipants(Long ownerId, Long projectId, ParticipantDto... participantDto);


    /**
     * [프로젝트 파일 추가]
     */
    Project addMemberFile(Long ownerId, Long projectId, Long... memberFileIds);
}