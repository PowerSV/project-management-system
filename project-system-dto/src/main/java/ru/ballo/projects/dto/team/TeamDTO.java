package ru.ballo.projects.dto.team;

import ru.ballo.projects.dto.member.MemberRoleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект для взамидествия с командой")
public class TeamDTO {

    @Schema(description = "Идентификатор команды")
    private Long id;

    @Schema(description = "Идентификатор проекта, над которым работает команда")
    private Long projectId;

    @Schema(description = "Наименование проекта")
    private String projectName;

    @Schema(description = "Члены команды")
    private List<MemberRoleDTO> teamMembers;
}
