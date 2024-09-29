package com.override.orchestrator_service.service;

import com.override.dto.TransactionDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Suggestion;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.SuggestionRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionMapperTest {

    @Mock
    private SuggestionRepository suggestionRepository;

    @InjectMocks
    private TransactionMapper transactionMapper;

    @Test
    public void mapTransactionToDTOTest() {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();

        Suggestion suggestion = Suggestion.builder()
                .id(UUID.randomUUID())
                .transaction(transaction)
                .suggestedCategoryId(1L)
                .accuracy(0.9f)
                .build();

        when(suggestionRepository.findSuggestionByTransaction(transaction)).thenReturn(suggestion);

        TransactionDTO transactionDTO = transactionMapper.mapTransactionToDTO(transaction);
        Assertions.assertEquals(transactionDTO.getId(), transaction.getId());
        Assertions.assertEquals(transactionDTO.getMessage(), transaction.getMessage());
        Assertions.assertEquals(transactionDTO.getDate(), transaction.getDate());
        Assertions.assertEquals(transactionDTO.getCategoryName(), transaction.getCategory().getName());
        Assertions.assertEquals(transactionDTO.getTelegramUserId(), transaction.getTelegramUserId());
        Assertions.assertEquals(transactionDTO.getSuggestedCategoryId(), suggestion.getSuggestedCategoryId());
        Assertions.assertEquals(transactionDTO.getAmount(), transaction.getAmount());
    }

    @Test
    public void mapTransactionToTelegramResponseWhenCategoryUndefined() throws InstanceNotFoundException {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        transaction.setCategory(null);

        TransactionResponseDTO transactionResponseDTO = transactionMapper.mapTransactionToTelegramResponse(transaction);
        Assertions.assertEquals(transactionResponseDTO.getType(), Type.EXPENSE.getValue());
        Assertions.assertEquals(transactionResponseDTO.getComment(), transaction.getMessage());
        Assertions.assertEquals(transactionResponseDTO.getCategory(), "Нераспознанное");
        Assertions.assertEquals(transactionResponseDTO.getAmount(), transaction.getAmount().toString());
    }

    @Test
    public void mapTransactionToTelegramResponseWhenCategoryIncome() throws InstanceNotFoundException {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        transaction.getCategory().setType(Type.INCOME);

        TransactionResponseDTO transactionResponseDTO = transactionMapper.mapTransactionToTelegramResponse(transaction);
        Assertions.assertEquals(transactionResponseDTO.getType(), Type.INCOME.getValue());
        Assertions.assertEquals(transactionResponseDTO.getComment(), transaction.getMessage());
        Assertions.assertEquals(transactionResponseDTO.getCategory(), transaction.getCategory().getName());
        Assertions.assertEquals(transactionResponseDTO.getAmount(), transaction.getAmount().toString());
    }

    @ParameterizedTest
    @MethodSource("provideAmountsString")
    public void mapTransactionToTelegramResponseRoundAmountTest(double amount, String resultRoundAmount) throws InstanceNotFoundException {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        transaction.setAmount(amount);

        TransactionResponseDTO transactionResponseDTO = transactionMapper.mapTransactionToTelegramResponse(transaction);

        Assertions.assertEquals(transactionResponseDTO.getAmount(), resultRoundAmount);
    }

    @ParameterizedTest
    @MethodSource("provideAmountsDouble")
    public void mapTransactionToDTORoundAmountTest(double amount, double resultRoundAmount) {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        transaction.setAmount(amount);

        Suggestion suggestion = Suggestion.builder()
                .id(UUID.randomUUID())
                .transaction(transaction)
                .suggestedCategoryId(1L)
                .accuracy(0.9f)
                .build();

        when(suggestionRepository.findSuggestionByTransaction(transaction)).thenReturn(suggestion);

        TransactionDTO transactionDTO = transactionMapper.mapTransactionToDTO(transaction);

        Assertions.assertEquals(transactionDTO.getAmount(), resultRoundAmount);
    }

    private static Stream<Arguments> provideAmountsDouble() {
        return Stream.of(
                Arguments.of(200.45678d, 200.46d),
                Arguments.of(188926.54d, 188926.54d)
        );
    }

    private static Stream<Arguments> provideAmountsString() {
        return Stream.of(
                Arguments.of(200.45678d, "200.46"),
                Arguments.of(188926.54d, "188926.54")
        );
    }
}
