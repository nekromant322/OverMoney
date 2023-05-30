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
import static com.override.orchestrator_service.utils.TestFieldsUtil.generateTestCategory;
import static java.util.Objects.*;
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
    public void processTransactionTest(TransactionMessageDTO transactionMessageDTO, Transaction transactionProvided) throws InstanceNotFoundException {
        OverMoneyAccount account = generateTestAccount();
        when(overMoneyAccountService.getOverMoneyAccountByChatId(transactionMessageDTO.getChatId())).thenReturn(account);
        Transaction transactionTest = transactionProcessingService.processTransaction(transactionMessageDTO);

        assertEquals(transactionProvided.getMessage(), transactionTest.getMessage());
        assertEquals(transactionProvided.getAmount(), transactionTest.getAmount());
        if (transactionProvided.getCategory() != null || transactionTest.getCategory() != null) {
            assertEquals(requireNonNull(transactionProvided.getCategory()).getName(), requireNonNull(transactionTest.getCategory()).getName());
        }
    }

    private static Stream<Arguments> provideTransactionArguments() {
        return Stream.of(
                Arguments.of(
                        TransactionMessageDTO.builder()
                                .message("пиво 200")
                                .username("kyomexd")
                                .chatId(404723191L)
                                .build(),
                        Transaction.builder()
                                .message("пиво")
                                .amount(200f)
                                .category(generateTestCategory())
                                .build()
                ),
                Arguments.of(
                        TransactionMessageDTO.builder()
                                .message("пиво7 200")
                                .username("kyomexd")
                                .chatId(404723191L)
                                .build(),
                        Transaction.builder()
                                .message("пиво7")
                                .amount(200f)
                                .build()
                ),
                Arguments.of(
                        TransactionMessageDTO.builder()
                                .message("пиво7 200")
                                .username("kyomexd")
                                .chatId(404723191L)
                                .build(),
                        Transaction.builder()
                                .message("пиво7")
                                .amount(200f)
                                .build()
                ),
                Arguments.of(
                        TransactionMessageDTO.builder()
                                .message("продукты 200")
                                .username("kyomexd")
                                .chatId(404723191L)
                                .build(),
                        Transaction.builder()
                                .message("продукты")
                                .category(generateTestCategory())
                                .amount(200f)
                                .build()
                )
        );
    }
}
