package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.feign.RecognizerFeign;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.utils.TestFieldsUtil;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.override.orchestrator_service.utils.TestFieldsUtil.generateTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransactionProcessingServiceTest {
    @InjectMocks
    private TransactionProcessingService transactionProcessingService;

    @Mock
    private OverMoneyAccountService overMoneyAccountService;

    @Mock
    private RecognizerFeign recognizerFeign;

    @Mock
    private CategoryService categoryService;

    @ParameterizedTest
    @MethodSource("provideTransactionArgumentsCauseExc")
    public void checkTransactionTypeThrowsException(String message) throws InstanceNotFoundException {
        TransactionMessageDTO transactionMessageDTO = TransactionMessageDTO.builder()
                .message(message)
                .userId(123L)
                .chatId(404723191L)
                .build();
        OverMoneyAccount account = generateTestAccount();
        List<CategoryDTO> categories = List.of(TestFieldsUtil.generateTestCategoryDTO());
        when(recognizerFeign.recognizeCategory(any(), any(), any())).thenReturn(TestFieldsUtil.generateTestCategoryDTO());
        when(categoryService.findCategoriesListByUserId(transactionMessageDTO.getChatId())).thenReturn(categories);
        when(overMoneyAccountService.getOverMoneyAccountByChatId(transactionMessageDTO.getChatId())).thenReturn(account);

        assertThrows(InstanceNotFoundException.class, () ->
                transactionProcessingService.processTransaction(transactionMessageDTO));

    }

    private static Stream<Arguments> provideTransactionArgumentsCauseExc() {
        return Stream.of(
                Arguments.of("пиво"),
                Arguments.of("пиво 200 пиво"),
                Arguments.of("пиво200"),
                Arguments.of("200пиво"),
                Arguments.of("пиво200 пиво"),
                Arguments.of("пиво пиво200"),
                Arguments.of("200пиво пиво"),
                Arguments.of("пиво 200пиво"),

                Arguments.of("200пиво 200 пиво"),
                Arguments.of("200.5пиво 200.5 пиво"),
                Arguments.of("200,5пиво 200,5 пиво"),

                Arguments.of("пиво 200 пиво200"),
                Arguments.of("пиво 200.5 пиво200.5"),
                Arguments.of("пиво 200,5 пиво200,5"),

                Arguments.of("200пиво 200 пиво200"),
                Arguments.of("200.5пиво 200.5 пиво200.5"),
                Arguments.of("200,5пиво 200,5 пиво200,5"),

                Arguments.of("100+100"),
                Arguments.of("100,1+100,5пиво 100,5+100,5"),
                Arguments.of("пиво 100+1-1"),
                Arguments.of("пиво 100+1/1"),
                Arguments.of("пиво 100+1*1"),
                Arguments.of("пиво 100+1-1"),
                Arguments.of("200+один+200 пиво"),
                Arguments.of("200+1 +один пиво")
        );
    }



    @ParameterizedTest
    @MethodSource("provideTransactionArguments")
    public void processTransactionTest(String message, String messageResponse, Float amount, String categoryName) throws InstanceNotFoundException {
        TransactionMessageDTO transactionMessageDTO = TransactionMessageDTO.builder()
                .message(message)
                .userId(123L)
                .chatId(404723191L)
                .build();
        OverMoneyAccount account = generateTestAccount();
        List<CategoryDTO> categories = List.of(TestFieldsUtil.generateTestCategoryDTO());
        when(recognizerFeign.recognizeCategory(any(), any(), any())).thenReturn(TestFieldsUtil.generateTestCategoryDTO());
        when(categoryService.findCategoriesListByUserId(transactionMessageDTO.getChatId())).thenReturn(categories);
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
                Arguments.of("пиво .45", "пиво", .45f, "продукты"),
                Arguments.of("пиво ,45", "пиво", .45f, "продукты"),
                Arguments.of("пиво 1.5 .45", "пиво 1.5", .45f, "продукты"),
                Arguments.of("пиво 1.5 ,45", "пиво 1.5", .45f, "продукты"),
                Arguments.of("пиво 777 100", "пиво 777", 100f, null),
                Arguments.of("пиво 777 123.45", "пиво 777", 123.45f, null),
                Arguments.of("пиво 777 123,45", "пиво 777", 123.45f, null),
                Arguments.of("пиво7 200", "пиво7", 200f, null),
                Arguments.of("пиво7 123.45", "пиво7", 123.45f, null),
                Arguments.of("пиво7 123,45", "пиво7", 123.45f, null),
                Arguments.of("7пиво 100", "7пиво", 100f, null),
                Arguments.of("продукты 200", "продукты", 200f, "продукты"),
                Arguments.of("пиво! 100", "пиво!", 100f, null),
                Arguments.of("пиво теплое 200", "пиво теплое", 200f, null),
                Arguments.of("пиво 777 теплое 200", "пиво 777 теплое", 200f, null),
                Arguments.of("пиво 777 ! теплое 200", "пиво 777 ! теплое", 200f, null),
                Arguments.of("пиво теплое 123.45", "пиво теплое", 123.45f, null),
                Arguments.of("пиво теплое 123,45", "пиво теплое", 123.45f, null),
                Arguments.of("пиво теплое 123.45", "пиво теплое", 123.45f, "продукты"),
                Arguments.of("пиво теплое 123,45", "пиво теплое", 123.45f, "продукты"),
                Arguments.of("пиво теплое 500", "пиво теплое", 500f, "продукты"),
                Arguments.of("пиво теплое 777 500", "пиво теплое 777", 500f, "продукты"),
                Arguments.of("пиво теплое 1.5 500", "пиво теплое 1.5", 500f, "продукты"),
                Arguments.of("пиво теплое 1.5 500.23", "пиво теплое 1.5", 500.23f, "продукты"),
                Arguments.of("пиво теплое 1.5 500,23", "пиво теплое 1.5", 500.23f, "продукты"),
                Arguments.of("пиво теплое! 1.5 500", "пиво теплое! 1.5", 500f, "продукты"),
                Arguments.of("пиво теплое! 1.5 500.23", "пиво теплое! 1.5", 500.23f, "продукты"),
                Arguments.of("пиво теплое! 1.5 500,23", "пиво теплое! 1.5", 500.23f, "продукты"),

                Arguments.of("200 пиво", "пиво", 200f, "продукты"),
                Arguments.of(".45 пиво", "пиво", .45f, "продукты"),
                Arguments.of(",45 пиво", "пиво", .45f, "продукты"),
                Arguments.of("1.5 .45 пиво", ".45 пиво", 1.5f, "продукты"),
                Arguments.of("1,5 .45 пиво", ".45 пиво", 1.5f, "продукты"),
                Arguments.of("100 пиво 777", "пиво 777", 100f, null),
                Arguments.of("123.45 пиво 777", "пиво 777", 123.45f, null),
                Arguments.of("123,45 пиво 777", "пиво 777", 123.45f, null),
                Arguments.of("200 пиво7", "пиво7", 200f, null),
                Arguments.of("123.45 пиво7", "пиво7", 123.45f, null),
                Arguments.of("123,45 пиво7", "пиво7", 123.45f, null),
                Arguments.of("100 7пиво", "7пиво", 100f, null),
                Arguments.of("продукты 200", "продукты", 200f, "продукты"),
                Arguments.of("100 пиво!", "пиво!", 100f, null),
                Arguments.of("200 пиво теплое", "пиво теплое", 200f, null),
                Arguments.of("200 пиво 777 теплое", "пиво 777 теплое", 200f, null),
                Arguments.of("200 пиво 777 ! теплое", "пиво 777 ! теплое", 200f, null),
                Arguments.of("123.45 пиво теплое", "пиво теплое", 123.45f, null),
                Arguments.of("123,45 пиво теплое", "пиво теплое", 123.45f, null),
                Arguments.of("123.45 пиво теплое", "пиво теплое", 123.45f, "продукты"),
                Arguments.of("123,45 пиво теплое", "пиво теплое", 123.45f, "продукты"),
                Arguments.of("500 пиво теплое", "пиво теплое", 500f, "продукты"),
                Arguments.of("500 пиво теплое 777", "пиво теплое 777", 500f, "продукты"),
                Arguments.of("500 пиво теплое 1.5", "пиво теплое 1.5", 500f, "продукты"),
                Arguments.of("500 пиво теплое! 1.5", "пиво теплое! 1.5", 500f, "продукты"),

                Arguments.of("200+200 пиво теплое", "пиво теплое", 400f, "продукты"),
                Arguments.of("пиво теплое 200+200", "пиво теплое", 400f, "продукты"),
                Arguments.of("200.1+200.1 пиво теплое+777", "пиво теплое+777", 400.2f, "продукты"),
                Arguments.of("200,1+200.1 пиво теплое+1+1", "пиво теплое+1+1", 400.2f, "продукты"),
                Arguments.of("200,1+200.1 пиво теплое 1 +1", "пиво теплое 1 +1", 400.2f, "продукты"),
                Arguments.of("200+200,1+200.1+200 пиво теплое! 1.5", "пиво теплое! 1.5", 800.2f, "продукты"),
                Arguments.of("пиво теплое! 1.5 200  + 200,1+200.1+200", "пиво теплое! 1.5", 800.2f, "продукты"),
                Arguments.of("пиво теплое! 1.5 200+   200,12  +   200.13  +200", "пиво теплое! 1.5", 800.25f, "продукты"),
                Arguments.of("пиво теплое! 1,5 200+   200,12  +   200.13  +200", "пиво теплое! 1,5", 800.25f, "продукты")

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
                .id(12345L)
                .name("продукты")
                .type(Type.EXPENSE)
                .keywords(keywordSet)
                .build();
    }

    private Keyword generateTestKeyword() {
        return Keyword.builder()
                .keywordId(new KeywordId("пиво", 123L))
                .category(Category.builder()
                        .id(12345L)
                        .name("продукты")
                        .type(Type.EXPENSE)
                        .build())
                .build();
    }
}
