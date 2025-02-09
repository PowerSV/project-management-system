package ru.ballo.projects.mapping;

import ru.ballo.projects.dto.project.ProjectDTO;
import ru.ballo.projects.models.Project;
import ru.ballo.projects.models.statuses.ProjectStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ProjectMapper {

    private final TeamMapper teamMapper;

    public ProjectMapper(TeamMapper teamMapper) {
        this.teamMapper = teamMapper;
    }

    public Project create(ProjectDTO dto) {
        log.info("Creating new project");
        Project newProject = new Project();
        newProject.setName(dto.getName());
        newProject.setDescription(dto.getDescription());
        newProject.setProjectStatus(ProjectStatus.DRAFT);
        log.debug("New project created");
        return newProject;
    }

    public ProjectDTO map(Project project) {
        ProjectDTO projectDTO = new ProjectDTO(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getProjectStatus().toString(),
                teamMapper.map(project.getTeam()));
        log.info("Mapping project to DTO: {}", projectDTO);
        return projectDTO;
    }
}
