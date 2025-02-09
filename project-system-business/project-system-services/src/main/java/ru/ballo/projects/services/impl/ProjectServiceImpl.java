package ru.ballo.projects.services.impl;

import ru.ballo.projects.dto.project.ProjectDTO;
import ru.ballo.projects.dto.project.ProjectFilter;
import ru.ballo.projects.dto.team.TeamDTO;
import ru.ballo.projects.mapping.ProjectMapper;
import ru.ballo.projects.mapping.TeamMapper;
import ru.ballo.projects.models.Project;
import ru.ballo.projects.models.Team;
import ru.ballo.projects.models.statuses.ProjectStatus;
import ru.ballo.projects.repos.JpaRepos.ProjectJpaRepository;
import ru.ballo.projects.repos.JpaRepos.TeamJpaRepository;
import ru.ballo.projects.repos.specifications.ProjectSpecification;
import ru.ballo.projects.services.ProjectService;
import ru.ballo.projects.services.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ProjectServiceImpl implements ProjectService {

    private final ProjectJpaRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TeamJpaRepository teamRepository;
    private final TeamMapper teamMapper;
    private final TeamService teamService;

    @Override
    public List<ProjectDTO> getAll() {
        log.info("Getting all projects");
        return projectRepository.findAll().stream()
                .map(projectMapper::map)
                .toList();
    }

    @Override
    public ProjectDTO deleteFromStorage(Long id) {
        log.info("Deleting project with ID: {}", id);
        Project deletedProject = projectRepository.findById(id).orElseThrow();
        if (deletedProject.getTeam() != null) {
            teamService.deleteFromStorage(deletedProject.getTeam().getId());
        }
        projectRepository.deleteProjectById(id);
        log.info("Deleted project");
        return projectMapper.map(deletedProject);
    }

    @Override
    public ProjectDTO create(ProjectDTO dto) {
        log.info("Creating project");
        Project project = projectMapper.create(dto);
        project = projectRepository.save(project);

        TeamDTO teamDTO = dto.getTeamDTO();
        if (teamDTO != null) {
            if (teamDTO.getId() == null) {
                Team team = teamMapper.create(teamDTO);
                team.setProject(project);
                project.setTeam(team);
                teamRepository.save(team);
            } else {
                project.setTeam(teamRepository.findById(teamDTO.getId())
                        .orElseThrow());
            }
            project = projectRepository.save(project);
        }
        log.info("Created project: {}", project.getId());
        return projectMapper.map(project);
    }

    @Override
    public ProjectDTO get(Long id) {
        log.info("Getting project with ID: {}", id);
        return projectRepository.findById(id)
                .map(projectMapper::map)
                .orElse(new ProjectDTO());
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) {
        log.info("Updating project with ID: {}", dto.getId());
        Project project = projectRepository.findById(dto.getId()).orElseThrow();
        if (!dto.getName().isBlank()) {
            project.setName(dto.getName());
        }
        if (!dto.getDescription().isBlank()) {
            project.setDescription(dto.getDescription());
        }
        project = projectRepository.save(project);
        log.info("Updated project: {}", project.getId());
        return projectMapper.map(project);
    }

    @Override
    public List<ProjectDTO> search(ProjectFilter filter) {
        log.info("Searching projects with filter: {}", filter);
        Specification<Project> spec = ProjectSpecification.searchByFilter(filter);
        List<ProjectDTO> matchedProjects = projectRepository.findAll(spec)
                .stream()
                .map(projectMapper::map)
                .collect(Collectors.toList());
        log.info("Search result: {}", matchedProjects);
        return matchedProjects;
    }

    @Override
    public ProjectDTO setTeam(TeamDTO teamDTO, Long projectId) {
        log.info("Setting team {} \n for project with ID: {}", teamDTO, projectId);
        Project project = projectRepository.findById(projectId).orElseThrow();
        Team team = teamRepository.findById(teamDTO.getId()).orElse(teamMapper.create(teamDTO));
        project.setTeam(team);
        team.setProject(project);
        project = projectRepository.save(project);
        teamRepository.save(team);
        log.info("Project with team: {}", projectId);
        return projectMapper.map(project);
    }

    @Override
    public ProjectDTO updateStatus(Long id) {
        log.info("Updating status for project with ID: {}", id);
        Project project = projectRepository.findById(id).orElseThrow();
        ProjectStatus currentStatus = project.getProjectStatus();
        ProjectStatus newStatus = getNextStatus(currentStatus);
        project.setProjectStatus(newStatus);
        project = projectRepository.save(project);
        log.info("New project status: {}", newStatus);
        return projectMapper.map(project);
    }

    private ProjectStatus getNextStatus(ProjectStatus status) {
        return switch (status) {
            case DRAFT -> ProjectStatus.IN_DEVELOPMENT;
            case IN_DEVELOPMENT -> ProjectStatus.IN_TESTING;
            case IN_TESTING, COMPLETED -> ProjectStatus.COMPLETED;
        };
    }
}
