package ru.ballo.projects.repos.JpaRepos;

import ru.ballo.projects.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Member m WHERE m.id = :id")
    void deleteMemberById(@Param("id") Long id);

    void deleteByEmail(String email);

    Optional<Member> findMemberByAccount(String account);

    Optional<Member> findByEmail(String email);
}
