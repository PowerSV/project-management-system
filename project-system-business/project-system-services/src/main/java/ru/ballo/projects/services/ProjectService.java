package ru.ballo.projects.services;

import ru.ballo.projects.dto.project.ProjectDTO;
import ru.ballo.projects.dto.project.ProjectFilter;
import ru.ballo.projects.dto.team.TeamDTO;

import java.util.List;

public interface ProjectService extends Service<ProjectDTO, ProjectDTO> {

    List<ProjectDTO> search(ProjectFilter filter);
    ProjectDTO updateStatus(Long id);
    ProjectDTO setTeam(TeamDTO teamDTO, Long projectId);

}
