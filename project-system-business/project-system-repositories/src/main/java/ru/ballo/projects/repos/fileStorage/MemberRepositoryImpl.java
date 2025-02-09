package ru.ballo.projects.repos.fileStorage;

import ru.ballo.projects.dto.member.SearchMemberFilter;
import ru.ballo.projects.models.Member;
import ru.ballo.projects.repos.AbstractMemberRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class MemberRepositoryImpl implements AbstractMemberRepository<Member> {

    private final List<Member> members;
    private final Path fileStoragePath = Path.of("data-storage.txt");
    private final AtomicLong idGenerator;

    public MemberRepositoryImpl() {
        byte[] temp;
        try {
            temp = Files.readAllBytes(fileStoragePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (temp.length != 0) {
            members = loadMembers();
            idGenerator = new AtomicLong(
                    members.stream()
                            .map(Member::getId)
                            .max(Long::compareTo)
                            .orElse(0L)
            );
        } else {
            members = new ArrayList<>();
            idGenerator = new AtomicLong(0L);
        }

    }

    private List<Member> loadMembers() {
        List<Member> result = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(fileStoragePath))) {
            result = (ArrayList<Member>) ois.readObject();
        } catch (IOException | ClassCastException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void saveMembers(List<Member> members) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(fileStoragePath))) {
            oos.writeObject(members);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Member create(Member member) {
        member.setId(idGenerator.incrementAndGet());
        members.add(member);
        saveMembers(members);
        return member;
    }

    @Override
    public Member update(Member member) {
        removeMemberFromCache(member.getId());
        members.add(member);
        saveMembers(members);
        return member;
    }

    @Override
    public Member deleteById(Long id) {
        Member memberToDelete = removeMemberFromCache(id);
        saveMembers(members);
        return memberToDelete;
    }

    private Member removeMemberFromCache(Long id) {
        Member memberToRemove = members.stream()
                .filter(m -> m.getId().equals(id))
                .findAny()
                .orElseThrow(RuntimeException::new);
        members.remove(memberToRemove);
        return memberToRemove;
    }

    @Override
    public Optional<Member> getById(Long id) {
        return members.stream()
                .filter(m -> m.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Member> getAll() {
        return members;
    }

    @Override
    public List<Member> searchMembers(SearchMemberFilter filter) {
        return null;
    }
}
