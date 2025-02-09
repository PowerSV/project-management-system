package ru.ballo.projects.repos.specifications;

import ru.ballo.projects.dto.project.ProjectFilter;
import ru.ballo.projects.models.Project;
import ru.ballo.projects.models.statuses.ProjectStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification {
    public static Specification<Project> searchByFilter(ProjectFilter filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getKeyword() != null && !filter.getKeyword().isEmpty()) {
                String keyword = "%" + filter.getKeyword() + "%";
                Predicate keywordPredicate = builder.or(
                        builder.like(root.get("name"), keyword),
                        builder.like(root.get("description"), keyword)
                );
                predicates.add(keywordPredicate);
            }

            if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
                List<ProjectStatus> statusList = filter.getStatuses()
                        .stream()
                        .map(s -> ProjectStatus.valueOf(s.toUpperCase()))
                        .toList();

                Predicate statusPredicate = root.get("projectStatus").in(statusList);
                predicates.add(statusPredicate);
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
