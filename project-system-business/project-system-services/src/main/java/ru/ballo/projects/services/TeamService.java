package ru.ballo.projects.services;

import ru.ballo.projects.dto.team.AddMemberDTO;
import ru.ballo.projects.dto.team.RemoveMemberDTO;
import ru.ballo.projects.dto.team.TeamDTO;

public interface TeamService extends Service<TeamDTO, TeamDTO> {
    TeamDTO addMember(AddMemberDTO dto);
    TeamDTO removeMember(RemoveMemberDTO dto);
}
