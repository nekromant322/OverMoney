package com.override.orchestrator_service.service;

import com.override.dto.TransactionMessageDTO;
import com.override.dto.constants.Type;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.override.orchestrator_service.utils.TestFieldsUtil.generateTestUser;
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
                Arguments.of("продукты 200", "продукты", 200f, "продукты"),
                Arguments.of("пиво7 123.45", "пиво7", 123.45f, null),
                Arguments.of("пиво7 123,45", "пиво7", 123.45f, null),
                Arguments.of("7пиво 100", "7пиво", 100f, null)
        );
    }

    private OverMoneyAccount generateTestAccount() {
        Set<Category> categorySet = new HashSet<>();
        categorySet.add(generateTestCategory());

        Set<User> userSet = new HashSet<>();
        userSet.add(generateTestUser());

        return OverMoneyAccount.builder()
                .id(1L)
                .chatId(404723191L)
                .categories(categorySet)
                .users(userSet)
                .build();
    }

    private Category generateTestCategory() {
        Set<Keyword> keywordSet = new HashSet<>();
        keywordSet.add(generateTestKeyword());

        return Category.builder()
                .id(UUID.fromString("6060677e-a3d5-4613-9da2-d067597ff095"))
                .name("продукты")
                .type(Type.EXPENSE)
                .keywords(keywordSet)
                .build();
    }

    private Keyword generateTestKeyword() {
        return Keyword.builder()
                .id(UUID.fromString("6620d7e6-c60f-4928-94d7-40ac77c24fc6"))
                .keyword("пиво")
                .category(Category.builder()
                        .id(UUID.fromString("6060677e-a3d5-4613-9da2-d067597ff095"))
                        .name("продукты")
                        .type(Type.EXPENSE)
                        .build())
                .build();
    }
}
