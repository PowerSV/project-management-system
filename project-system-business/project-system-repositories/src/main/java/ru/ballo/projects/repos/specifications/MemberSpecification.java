package ru.ballo.projects.repos.specifications;

import ru.ballo.projects.models.Member;
import ru.ballo.projects.models.statuses.MemberStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecification {
    public static Specification<Member> searchByFilter(String filter) {
        return ((root, query, builder) -> {
            Predicate isActive = builder.equal(root.get("status"), MemberStatus.ACTIVE);

            String likeKeyword = "%" + filter.toLowerCase() + "%";
            Predicate matchLastName = builder.like(builder.lower(root.get("lastName")), likeKeyword);
            Predicate matchFirstName = builder.like(builder.lower(root.get("firstName")), likeKeyword);
            Predicate matchMiddleName = builder.like(builder.lower(root.get("middleName")), likeKeyword);
            Predicate matchAccount = builder.like(builder.lower(root.get("account")), likeKeyword);
            Predicate matchEmail = builder.like(builder.lower(root.get("email")), likeKeyword);
            Predicate matchTextField = builder.or(
                    matchLastName, matchFirstName, matchMiddleName, matchAccount, matchEmail);

            return builder.and(isActive, matchTextField);
        });
    }
}
