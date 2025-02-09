package ru.ballo.projects.repos.JpaRepos;

import ru.ballo.projects.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectJpaRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Project p WHERE p.id = :id")
    void deleteProjectById(@Param("id") Long id);
}
