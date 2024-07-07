package com.override.orchestrator_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.override.dto.AmountRangeDTO;
import com.override.dto.DateRangeDTO;
import com.override.dto.TransactionFilterDTO;
import com.override.orchestrator_service.filter.TransactionFilter;
import com.override.orchestrator_service.mapper.TransactionFilterMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TransactionFilterMapperTest {

    @InjectMocks
    private TransactionFilterMapper transactionFilterMapper;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void mapStringJsonToTransactionFilterWhenValidJsonProvidedShouldReturnTransactionFilter() throws IOException {
        String filterJson = "{\"category\":\"Продукты\"," +
                "\"amount\":{\"start\":5,\"end\":1000}," +
                "\"message\":\"пиво\"," +
                "\"date\":{\"start\":\"2023-07-04T20:00:00.000000\",\"end\":\"2024-07-06T00:00:00.000000\"}," +
                "\"telegramUserName\":[\"Marandyuk_Anatolii\",\"Kulpinov_Evgeny\"]}";
        TransactionFilterDTO filterDTO = new TransactionFilterDTO();
        LocalDateTime start = LocalDateTime.of(2023, 9, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 9, 30, 23, 59);
        filterDTO.setCategory("Продукты");
        filterDTO.setAmount(new AmountRangeDTO(100, 500));
        filterDTO.setMessage("пиво");
        filterDTO.setDate(new DateRangeDTO(start, end));
        filterDTO.setTelegramUserNameList(Arrays.asList("Marandyuk_Anatolii", "Kulpinov_Evgeny"));

        Category category = new Category();
        category.setName("Продукты");

        given(objectMapper.readValue(filterJson, TransactionFilterDTO.class)).willReturn(filterDTO);
        given(categoryRepository.findCategoryByNameAndAccountId(1L, "Продукты")).willReturn(category);

        TransactionFilter filter = transactionFilterMapper.mapStringJsonToTransactionFilter(filterJson, 1L);

        assertThat(filter.getCategory()).isEqualTo(category);
        assertThat(filter.getAmount().getStart()).isEqualTo(100);
        assertThat(filter.getAmount().getEnd()).isEqualTo(500);
        assertThat(filter.getMessage()).isEqualTo("пиво");
        assertThat(filter.getDate().getStart()).isEqualTo(start);
        assertThat(filter.getDate().getEnd()).isEqualTo(end);
    }
}
