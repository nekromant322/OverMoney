package com.override.orchestrator_service.service;

import com.override.dto.TransactionMessageDTO;
import com.override.orchestrator_service.model.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.management.InstanceNotFoundException;
import java.util.stream.Stream;

import static com.override.orchestrator_service.utils.TestFieldsUtil.generateTestAccount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransactionProcessingServiceTest {
    @InjectMocks
    private TransactionProcessingService transactionProcessingService;

    @Mock
    private OverMoneyAccountService overMoneyAccountService;


    @ParameterizedTest
    @MethodSource("provideTransactionArguments")
    public void processTransactionTest(String message, String messageResponse, Float amount, String categoryName) throws InstanceNotFoundException {
        TransactionMessageDTO transactionMessageDTO = TransactionMessageDTO.builder()
                .message(message)
                .username("kyomexd")
                .chatId(404723191L)
                .build();
        OverMoneyAccount account = generateTestAccount();

        when(overMoneyAccountService.getOverMoneyAccountByChatId(transactionMessageDTO.getChatId())).thenReturn(account);
        Transaction transactionTest = transactionProcessingService.processTransaction(transactionMessageDTO);

        assertEquals(messageResponse, transactionTest.getMessage());
        assertEquals(amount, transactionTest.getAmount());
        if (categoryName != null && transactionTest.getCategory() != null) {
            assertEquals(categoryName, transactionTest.getCategory().getName());
        }
    }

    private static Stream<Arguments> provideTransactionArguments() {
        return Stream.of(
                Arguments.of("пиво 200", "пиво", 200f, "продукты"),
                Arguments.of("пиво7 200", "пиво7", 200f, null),
                Arguments.of("продукты 200", "продукты", 200f, "продукты")
        );
    }
}
