package ru.ballo.projects.services;

import ru.ballo.projects.dto.member.CreateUpdateMemberDTO;
import ru.ballo.projects.dto.member.MemberDTO;

import java.util.List;

public interface MemberService extends Service<MemberDTO, CreateUpdateMemberDTO> {
    MemberDTO delete(Long id);
    List<MemberDTO> search(String filter);
    MemberDTO getMember(String account);
}
