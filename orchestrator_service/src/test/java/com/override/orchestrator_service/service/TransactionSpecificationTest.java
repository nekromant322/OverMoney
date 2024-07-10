package com.override.orchestrator_service.service;

import com.override.dto.AmountRangeDTO;
import com.override.dto.DateRangeDTO;
import com.override.orchestrator_service.filter.TransactionFilter;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.specification.TransactionSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionSpecificationTest {

    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private CriteriaQuery<?> criteriaQuery;
    @Mock
    private Root<Transaction> root;
    @Mock
    private Path<Object> path;
    @Mock
    private Predicate predicate;

    @Test
    public void createSpecificationWhenFiltersAppliedShouldReturnSpecification() {
        Long accountId = 1L;
        TransactionFilter filters = new TransactionFilter();
        filters.setAmount(new AmountRangeDTO(100, 200));
        filters.setMessage("пиво");
        filters.setDate(new DateRangeDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        filters.setTelegramUserIdList(Arrays.asList(1L, 2L));

        doReturn(predicate).when(criteriaBuilder).equal(any(), eq(1L));
        when(criteriaBuilder.between(any(), any(Integer.class), any(Integer.class))).thenReturn(predicate);
        when(criteriaBuilder.between(any(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(predicate);
        when(criteriaBuilder.like(any(), any(String.class))).thenReturn(predicate);
        when(path.in(anyList())).thenReturn(predicate);
        when(root.get(anyString())).thenReturn(path);

        Specification<Transaction> specification = TransactionSpecification.createSpecification(accountId, filters);
        Predicate resultPredicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertThat(specification).isNotNull();
    }
}