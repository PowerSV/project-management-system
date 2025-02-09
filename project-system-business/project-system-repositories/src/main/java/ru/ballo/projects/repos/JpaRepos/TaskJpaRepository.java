package ru.ballo.projects.repos.JpaRepos;

import ru.ballo.projects.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskJpaRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Task t WHERE t.id = :id")
    void deleteTaskById(@Param("id") Long id);
}
