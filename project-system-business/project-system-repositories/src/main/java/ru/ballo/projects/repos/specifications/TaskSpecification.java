package ru.ballo.projects.repos.specifications;

import ru.ballo.projects.dto.task.TaskFilter;
import ru.ballo.projects.models.Task;
import ru.ballo.projects.models.statuses.TaskStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {
    public static Specification<Task> searchByFilter(TaskFilter filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по текстовому значению (Наименование задачи)
            if (filter.getKeyword() != null && !filter.getKeyword().isEmpty()) {
                String keyword = "%" + filter.getKeyword() + "%";
                predicates.add(builder.like(builder.lower(root.get("name")), keyword.toLowerCase()));
            }

            // Фильтр по статусам задачи
            if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
                List<TaskStatus> statusList = filter.getStatuses()
                        .stream()
                        .map(s -> TaskStatus.valueOf(s.toUpperCase()))
                        .toList();
                predicates.add(root.get("status").in(statusList));
            }

            // Фильтр по исполнителю
            if (filter.getAssigneeId() != null) {
                predicates.add(builder.equal(root.get("assignee").get("id"), filter.getAssigneeId()));
            }

            // Фильтр по автору задачи
            if (filter.getAuthorId() != null) {
                predicates.add(builder.equal(root.get("author").get("id"), filter.getAuthorId()));
            }

            // Фильтр по периоду крайнего срока задачи
            if (filter.getDeadlinePeriodStart() != null && filter.getDeadlinePeriodEnd() != null) {
                predicates.add(builder.between(root.get("deadline"),
                        filter.getDeadlinePeriodStart(),
                        filter.getDeadlinePeriodEnd())
                );
            }

            // Фильтр по периоду создания задачи
            if (filter.getCreationPeriodStart() != null && filter.getCreationPeriodEnd() != null) {
                predicates.add(builder.between(root.get("creationDate"),
                        filter.getCreationPeriodStart(),
                        filter.getCreationPeriodEnd())
                );
            }

            // Сортировка по дате создания в обратном порядке (сначала свежие задачи)
            query.orderBy(builder.desc(root.get("creationDate")));

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
