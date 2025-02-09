package ru.ballo.projects.services.impl;

import ru.ballo.projects.dto.member.CreateUpdateMemberDTO;
import ru.ballo.projects.dto.member.MemberDTO;
import ru.ballo.projects.mapping.MemberMapper;
import ru.ballo.projects.models.Member;
import ru.ballo.projects.models.statuses.MemberStatus;
import ru.ballo.projects.repos.JpaRepos.MemberJpaRepository;
import ru.ballo.projects.repos.specifications.MemberSpecification;
import ru.ballo.projects.services.MemberService;
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
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberJpaRepository memberRepository;

    @Override
    public MemberDTO create(CreateUpdateMemberDTO newMember) {
        log.info("Creating a new member: {}", newMember);
        Member member = memberMapper.create(newMember);
        member.setAccount(getAccount(newMember));
        member = memberRepository.save(member);
        log.info("New member created");
        return memberMapper.map(member);
    }

    @Override
    public MemberDTO update(CreateUpdateMemberDTO dto) {
        log.info("Updating member with ID: {}", dto.getId());
        Member member = memberRepository.findById(dto.getId()).orElseThrow();
        if (!member.getStatus().equals(MemberStatus.ACTIVE)) {
            log.error("Member status should be Active but was DELETED");
            throw new RuntimeException("Нельзя изменить удаленного сотрудника");
        }
        if (dto.getEmail() != null) {
            member.setEmail(dto.getEmail());
        }
        if (dto.getLastName() != null) {
            member.setLastName(dto.getLastName());
        }
        if (dto.getFirstName() != null) {
            member.setFirstName(dto.getFirstName());
        }
        if (dto.getMiddleName() != null) {
            member.setMiddleName(dto.getMiddleName());
        }
        if (dto.getAccount() != null) {
            member.setAccount(getAccount(dto));
        }
        member = memberRepository.save(member);
        log.info("Member updated");
        return memberMapper.map(member);
    }

    public String getAccount(CreateUpdateMemberDTO dto) {
        String email = dto.getEmail();
        if (dto.getAccount() == null || dto.getAccount().isBlank()) {
            return email == null || email.isBlank() ? null : email.substring(0, email.indexOf("@"));
        }
        return dto.getAccount();
    }

    @Override
    public MemberDTO deleteFromStorage(Long id) {
        log.info("Deleting member from storage with ID: {}", id);
        Member deletedMember = memberRepository.findById(id).orElseThrow();
        memberRepository.deleteMemberById(id);
        log.info("Member deleted from storage");
        return memberMapper.map(deletedMember);
    }

    @Override
    public MemberDTO delete(Long id) {
        log.info("Deleting member with ID: {}", id);
        Member deletedMember = memberRepository.findById(id).orElseThrow();
        deletedMember.setStatus(MemberStatus.DELETED);
        deletedMember = memberRepository.save(deletedMember);
        log.info("Member deleted");
        return memberMapper.map(deletedMember);
    }

    @Override
    public List<MemberDTO> search(String filter) {
        log.info("Searching for members with filter: {}", filter);
        Specification<Member> spec = MemberSpecification.searchByFilter(filter);
        List<MemberDTO> matchedMembers = memberRepository.findAll(spec)
                .stream()
                .map(memberMapper::map)
                .collect(Collectors.toList());
        log.info("Found {} members", matchedMembers.size());
        return matchedMembers;
    }

    @Override
    public MemberDTO get(Long id) {
        log.info("Get member with ID: {}", id);
        return memberRepository.findById(id)
                .map(memberMapper::map)
                .orElse(new MemberDTO());
    }

    @Override
    public MemberDTO getMember(String account) {
        log.info("Get member with account: {}", account);
        return memberRepository.findMemberByAccount(account)
                .map(memberMapper::map)
                .orElse(new MemberDTO());
    }

    @Override
    public List<MemberDTO> getAll() {
        log.info("Get all members");
        List<MemberDTO> members = memberRepository.findAll().stream()
                .map(memberMapper::map)
                .toList();
        log.info("Fetched {} members", members.size());
        return members;
    }
}
