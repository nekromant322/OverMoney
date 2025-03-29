package com.override.orchestrator_service.repository.specification;

import com.override.orchestrator_service.filter.TransactionFilter;
import com.override.orchestrator_service.model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> createSpecification(Long accountId, TransactionFilter filters) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getCategory() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), filters.getCategory()));
            }

            if (filters.getAmount() != null) {
                predicates.add(criteriaBuilder.between(root.get("amount"), filters.getAmount().getBegin(),
                        filters.getAmount().getEnd()));
            }

            if (filters.getMessage() != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("message")),
                        "%" + filters.getMessage().toLowerCase() + "%"));
            }

            if (filters.getDate() != null) {
                predicates.add(criteriaBuilder.between(root.get("date"), filters.getDate().getBegin(),
                        filters.getDate().getEnd()));
            }

            if (filters.getTelegramUserIdList() != null && !filters.getTelegramUserIdList().isEmpty()) {
                predicates.add(root.get("telegramUserId").in(filters.getTelegramUserIdList()));
            }

            predicates.add(criteriaBuilder.equal(root.get("account").get("id"), accountId));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
