package ru.ballo.projects.integration.repos;

import ru.ballo.projects.integration.BaseTest;
import ru.ballo.projects.models.Member;
import ru.ballo.projects.models.statuses.MemberStatus;
import ru.ballo.projects.repos.JpaRepos.MemberJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
public class MemberRepoIT extends BaseTest {

    @Autowired
    MemberJpaRepository memberRepo;

    @Value("${spring.datasource.url}")
    private String url;

    @Test
    public void checkDatasourceUrl() {
        Assertions.assertNotEquals("jdbc:postgresql://localhost/postgres", url);
    }

    private Member createTestMember() {
        Member member = new Member();
        member.setFirstName("Test");
        member.setLastName("Test");
        member.setAccount("testAccount");
        member.setEmail("test@example.com");
        member.setStatus(MemberStatus.ACTIVE);
        return member;
    }

    @Test
    public void testSaveMember() {
        Member member = createTestMember();

        memberRepo.save(member);

        Assertions.assertNotNull(member.getId());

        Member savedMember = memberRepo.findById(member.getId()).orElse(null);
        Assertions.assertNotNull(savedMember);
        Assertions.assertEquals(member.getEmail(), savedMember.getEmail());
    }

    @Test
    void testSave_ExistingMember_ShouldUpdate() {
        Member member = createTestMember();
        Member savedMember = memberRepo.save(member);

        // Modify the member
        savedMember.setFirstName("UpdatedFirstName");
        savedMember.setLastName("UpdatedLastName");

        Member updatedMember = memberRepo.save(savedMember);

        Assertions.assertEquals("UpdatedFirstName", updatedMember.getFirstName());
        Assertions.assertEquals("UpdatedLastName", updatedMember.getLastName());
    }

    @Test
    void testSave_NullMember_ShouldThrowException() {
        Member member = createTestMember();
        member.setLastName(null);
        Assertions.assertThrows(RuntimeException.class, () -> memberRepo.save(member));
    }

    @Test
    void testFindMemberByAccount() {
        Member member = createTestMember();

        memberRepo.save(member);

        Optional<Member> foundMember = memberRepo.findMemberByAccount("testAccount");

        Assertions.assertTrue(foundMember.isPresent());
        Assertions.assertEquals("test@example.com", foundMember.get().getEmail());
    }

    @Test
    void testFindByEmail() {
        Member member = createTestMember();
        memberRepo.save(member);

        Optional<Member> foundMember = memberRepo.findByEmail("test@example.com");

        Assertions.assertTrue(foundMember.isPresent());
        Assertions.assertEquals("testAccount", foundMember.get().getAccount());
    }

    @Test
    void testDeleteMemberById() {
        Member member = createTestMember();

        Member savedMember = memberRepo.save(member);

        memberRepo.deleteMemberById(savedMember.getId());

        Assertions.assertFalse(memberRepo.existsById(savedMember.getId()));
    }

    @Test
    void testDeleteByEmail() {
        Member member = createTestMember();

        Member savedMember = memberRepo.save(member);

        memberRepo.deleteByEmail("test@example.com");

        Assertions.assertFalse(memberRepo.existsById(savedMember.getId()));
    }
}
