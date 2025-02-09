package ru.ballo.projects.services.impl;

import ru.ballo.projects.dto.team.AddMemberDTO;
import ru.ballo.projects.dto.team.RemoveMemberDTO;
import ru.ballo.projects.dto.team.TeamDTO;
import ru.ballo.projects.mapping.MemberMapper;
import ru.ballo.projects.mapping.TeamMapper;
import ru.ballo.projects.models.Member;
import ru.ballo.projects.models.Project;
import ru.ballo.projects.models.Team;
import ru.ballo.projects.models.TeamMember;
import ru.ballo.projects.repos.JpaRepos.MemberJpaRepository;
import ru.ballo.projects.repos.JpaRepos.ProjectJpaRepository;
import ru.ballo.projects.repos.JpaRepos.TeamJpaRepository;
import ru.ballo.projects.repos.JpaRepos.TeamMemberJpaRepository;
import ru.ballo.projects.services.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class TeamServiceImpl implements TeamService {

    private final TeamJpaRepository teamRepository;
    private final MemberJpaRepository memberRepository;
    private final ProjectJpaRepository projectRepository;
    private final TeamMapper teamMapper;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final MemberMapper memberMapper;

    @Override
    public List<TeamDTO> getAll() {
        log.info("Getting all teams");
        return teamRepository.findAll().stream()
                .map(teamMapper::map)
                .toList();
    }

    @Override
    public TeamDTO create(TeamDTO dto) {
        log.info("Creating team: {}", dto);
        Team newTeam = teamMapper.create(dto);
        newTeam = teamRepository.save(newTeam);
        if (!newTeam.getTeamMembers().isEmpty()) {
            teamMemberRepository.saveAll(newTeam.getTeamMembers());
        }
        log.info("Created team: {}", newTeam.getId());
        return teamMapper.map(newTeam);
    }

    @Override
    public TeamDTO update(TeamDTO teamDTO) {
        log.info("Updating team: {}", teamDTO.getId());
        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow();

        if (teamDTO.getProjectId() != null) {
            Project project = projectRepository.findById(teamDTO.getProjectId())
                    .orElseThrow();
            team.setProject(project);
        }
        if (teamDTO.getTeamMembers() != null) {
            log.info("Updating team members");
            teamMemberRepository.deleteByTeam(team);
            List<TeamMember> newTeamMembers = teamMapper.createNewTeamMembers(teamDTO.getTeamMembers(), team);
            team.setTeamMembers(newTeamMembers);
        }
        team = teamRepository.save(team);
        log.info("Updated team: {}", team.getId());
        return teamMapper.map(team);
    }

    @Override
    public TeamDTO get(Long id) {
        log.info("Getting team: {}", id);
        return teamRepository.findById(id)
                .map(teamMapper::map)
                .orElse(new TeamDTO());
    }

    @Override
    public TeamDTO addMember(AddMemberDTO dto) {
        log.info("Adding member to team: teamId={}, memberId={}", dto.getTeamId(), dto.getMemberId());
        Team team = teamRepository.findById(dto.getTeamId()).orElseThrow();
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow();

        TeamMember teamMember = memberMapper.createTeamMember(member, team, dto.getRole());
        teamMember = teamMemberRepository.save(teamMember);

        List<TeamMember> teamMemberList = team.getTeamMembers();
        teamMemberList.add(teamMember);
        team.setTeamMembers(teamMemberList);
        team = teamRepository.save(team);
        log.info("add member successfully. Team: {}", team.getId());
        return teamMapper.map(team);
    }

    @Override
    public TeamDTO removeMember(RemoveMemberDTO dto) {
        log.info("Removing member from team: teamId={}, memberId={}", dto.getTeamId(), dto.getMemberId());
        Team team = teamRepository.findById(dto.getTeamId()).orElseThrow();

        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow();

        if (team.getTeamMembers() != null) {
            List<TeamMember> teamMembers = team.getTeamMembers()
                    .stream()
                    .filter(tm -> !tm.getMember().equals(member))
                    .collect(Collectors.toList());

            teamMemberRepository.deleteByMember(member);

            team.setTeamMembers(teamMembers);
        }

        team = teamRepository.save(team);
        log.info("Remove member successfully. Team: {}", team.getId());
        return teamMapper.map(team);
    }

    @Override
    public TeamDTO deleteFromStorage(Long id) {
        log.info("Deleting team from storage: {}", id);
        Team deletedTeam = teamRepository.findById(id).orElseThrow();
        if (deletedTeam.getTeamMembers() != null) {
            teamMemberRepository.deleteAll(deletedTeam.getTeamMembers());
            deletedTeam.setTeamMembers(null);
        }
        teamRepository.deleteTeamById(id);
        log.info("Deleted team from storage: {}", deletedTeam.getId());
        return teamMapper.map(deletedTeam);
    }
}
