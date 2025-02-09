package ru.ballo.projects.repos;

import ru.ballo.projects.dto.member.SearchMemberFilter;
import ru.ballo.projects.models.Member;

import java.util.List;

public interface AbstractMemberRepository<M extends Member> extends Repository<M> {
    List<Member> searchMembers(SearchMemberFilter filter);
}
