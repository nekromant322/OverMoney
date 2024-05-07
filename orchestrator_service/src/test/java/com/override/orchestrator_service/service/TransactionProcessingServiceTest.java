package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import com.override.orchestrator_service.exception.TransactionProcessingException;
import com.override.orchestrator_service.feign.RecognizerFeign;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.util.TelegramUtils;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Test;
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
import java.security.Principal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

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

    @Mock
    private TelegramUtils telegramUtils;

    @ParameterizedTest
    @MethodSource("provideTransactionArgumentsCauseExc")
    public void checkProcessTransactionThrowsExceptionTest(String message) throws InstanceNotFoundException {
        transactionProcessingService.init();
        TransactionMessageDTO transactionMessageDTO = TransactionMessageDTO.builder()
                .message(message)
                .userId(123L)
                .chatId(404723191L)
                .build();
        OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        List<CategoryDTO> categories = List.of(TestFieldsUtil.generateTestCategoryDTO());
        when(recognizerFeign.recognizeCategory(any(), any(), any())).thenReturn(TestFieldsUtil.generateTestCategoryDTO());
        when(categoryService.findCategoriesListByUserId(transactionMessageDTO.getChatId())).thenReturn(categories);
        when(overMoneyAccountService.getOverMoneyAccountByChatId(transactionMessageDTO.getChatId())).thenReturn(account);

        assertThrows(TransactionProcessingException.class, () ->
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
    public void checkProcessTransactionReturnsCorrectValuesTest(String message, String messageResponse, double amount, String categoryName) throws InstanceNotFoundException {
        transactionProcessingService.init();
        TransactionMessageDTO transactionMessageDTO = TransactionMessageDTO.builder()
                .message(message)
                .userId(123L)
                .chatId(404723191L)
                .build();
        OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        List<CategoryDTO> categories = List.of(TestFieldsUtil.generateTestCategoryDTO());
        when(recognizerFeign.recognizeCategory(any(), any(), any())).thenReturn(TestFieldsUtil.generateTestCategoryDTO());
        when(categoryService.findCategoriesListByUserId(transactionMessageDTO.getChatId())).thenReturn(categories);
        when(overMoneyAccountService.getOverMoneyAccountByChatId(transactionMessageDTO.getChatId())).thenReturn(account);
        Transaction transactionTest = transactionProcessingService.processTransaction(transactionMessageDTO);

        assertEquals(messageResponse, transactionTest.getMessage());
        assertEquals(amount, transactionTest.getAmount(), 0.0001d);
        if (categoryName != null && transactionTest.getCategory() != null) {
            assertEquals(categoryName, transactionTest.getCategory().getName());
        }
    }

    private static Stream<Arguments> provideTransactionArguments() {
        return Stream.of(
                Arguments.of("пиво 200", "пиво", 200, "продукты"),
                Arguments.of("пиво .45", "пиво", .45, "продукты"),
                Arguments.of("пиво ,45", "пиво", .45, "продукты"),
                Arguments.of("пиво 1.5 .45", "пиво 1.5", .45, "продукты"),
                Arguments.of("пиво 1.5 ,45", "пиво 1.5", .45, "продукты"),
                Arguments.of("пиво 777 100", "пиво 777", 100, null),
                Arguments.of("пиво 777 123.45", "пиво 777", 123.45, null),
                Arguments.of("пиво 777 123,45", "пиво 777", 123.45, null),
                Arguments.of("пиво7 200", "пиво7", 200, null),
                Arguments.of("пиво7 123.45", "пиво7", 123.45, null),
                Arguments.of("пиво7 123,45", "пиво7", 123.45, null),
                Arguments.of("7пиво 100", "7пиво", 100, null),
                Arguments.of("продукты 200", "продукты", 200, "продукты"),
                Arguments.of("пиво! 100", "пиво!", 100d, null),
                Arguments.of("пиво теплое 200", "пиво теплое", 200, null),
                Arguments.of("пиво 777 теплое 200", "пиво 777 теплое", 200, null),
                Arguments.of("пиво 777 ! теплое 200", "пиво 777 ! теплое", 200, null),
                Arguments.of("пиво теплое 123.45", "пиво теплое", 123.45, null),
                Arguments.of("пиво теплое 123,45", "пиво теплое", 123.45, null),
                Arguments.of("пиво теплое 123.45", "пиво теплое", 123.45, "продукты"),
                Arguments.of("пиво теплое 123,45", "пиво теплое", 123.45, "продукты"),
                Arguments.of("пиво теплое 500", "пиво теплое", 500, "продукты"),
                Arguments.of("пиво теплое 777 500", "пиво теплое 777", 500, "продукты"),
                Arguments.of("пиво теплое 1.5 500", "пиво теплое 1.5", 500, "продукты"),
                Arguments.of("пиво теплое 1.5 500.23", "пиво теплое 1.5", 500.23, "продукты"),
                Arguments.of("пиво теплое 1.5 500,23", "пиво теплое 1.5", 500.23, "продукты"),
                Arguments.of("пиво теплое! 1.5 500", "пиво теплое! 1.5", 500, "продукты"),
                Arguments.of("пиво теплое! 1.5 500.23", "пиво теплое! 1.5", 500.23, "продукты"),
                Arguments.of("пиво теплое! 1.5 500,23", "пиво теплое! 1.5", 500.23, "продукты"),

                Arguments.of("200 пиво", "пиво", 200, "продукты"),
                Arguments.of(".45 пиво", "пиво", .45, "продукты"),
                Arguments.of(",45 пиво", "пиво", .45, "продукты"),
                Arguments.of("1.5 .45 пиво", ".45 пиво", 1.5, "продукты"),
                Arguments.of("1,5 .45 пиво", ".45 пиво", 1.5, "продукты"),
                Arguments.of("100 пиво 777", "пиво 777", 100, null),
                Arguments.of("123.45 пиво 777", "пиво 777", 123.45, null),
                Arguments.of("123,45 пиво 777", "пиво 777", 123.45, null),
                Arguments.of("200 пиво7", "пиво7", 200, null),
                Arguments.of("123.45 пиво7", "пиво7", 123.45, null),
                Arguments.of("123,45 пиво7", "пиво7", 123.45, null),
                Arguments.of("100 7пиво", "7пиво", 100, null),
                Arguments.of("продукты 200", "продукты", 200, "продукты"),
                Arguments.of("100 пиво!", "пиво!", 100, null),
                Arguments.of("200 пиво теплое", "пиво теплое", 200, null),
                Arguments.of("200 пиво 777 теплое", "пиво 777 теплое", 200, null),
                Arguments.of("200 пиво 777 ! теплое", "пиво 777 ! теплое", 200, null),
                Arguments.of("123.45 пиво теплое", "пиво теплое", 123.45, null),
                Arguments.of("123,45 пиво теплое", "пиво теплое", 123.45, null),
                Arguments.of("123.45 пиво теплое", "пиво теплое", 123.45, "продукты"),
                Arguments.of("123,45 пиво теплое", "пиво теплое", 123.45, "продукты"),
                Arguments.of("500 пиво теплое", "пиво теплое", 500, "продукты"),
                Arguments.of("500 пиво теплое 777", "пиво теплое 777", 500, "продукты"),
                Arguments.of("500 пиво теплое 1.5", "пиво теплое 1.5", 500, "продукты"),
                Arguments.of("500 пиво теплое! 1.5", "пиво теплое! 1.5", 500, "продукты"),

                Arguments.of("200+200 пиво теплое", "пиво теплое", 400, "продукты"),
                Arguments.of("пиво теплое 200+200", "пиво теплое", 400, "продукты"),
                Arguments.of("200.1+200.1 пиво теплое+777", "пиво теплое+777", 400.2, "продукты"),
                Arguments.of("200,1+200.1 пиво теплое+1+1", "пиво теплое+1+1", 400.2, "продукты"),
                Arguments.of("200,1+200.1 пиво теплое 1 +1", "пиво теплое 1 +1", 400.2, "продукты"),
                Arguments.of("200,1+200.1+200.12 пиво", "пиво", 600.32, "продукты"),
                Arguments.of("пиво 200,1 +200.1+ 200.12", "пиво", 600.32, "продукты"),
                Arguments.of("200+200,1+200.1+200 пиво теплое! 1.5", "пиво теплое! 1.5", 800.2, "продукты"),
                Arguments.of("пиво теплое! 1.5 200  + 200,1+200.1+200", "пиво теплое! 1.5", 800.2, "продукты"),
                Arguments.of("пиво теплое! 1.5 200+   200,12  +   200.13  +200", "пиво теплое! 1.5", 800.25, "продукты"),
                Arguments.of("пиво теплое! 1,5 200+   200,12  +   200.13  +200", "пиво теплое! 1,5", 800.25, "продукты")
        );
    }

    @Test
    public void checkValidateAndProcessTransactionWorksEqualsWithAndWithoutPrincipalTest() throws InstanceNotFoundException {
        transactionProcessingService.init();
        when(telegramUtils.getTelegramId(any())).thenReturn(TestFieldsUtil.generateTestAccount().getId());
        when(overMoneyAccountService.getAccountByUserId(any())).thenReturn(TestFieldsUtil.generateTestAccount());

        List<CategoryDTO> categories = List.of(TestFieldsUtil.generateTestCategoryDTO());
        when(recognizerFeign.recognizeCategory(any(), any(), any())).thenReturn(TestFieldsUtil.generateTestCategoryDTO());
        when(categoryService.findCategoriesListByUserId(any())).thenReturn(categories);
        when(overMoneyAccountService.getOverMoneyAccountByChatId(any())).thenReturn(TestFieldsUtil.generateTestAccount());
        Principal principal = new JwtAuthentication();

        Transaction resultTransactionWithPrincipal =
                transactionProcessingService
                        .validateAndProcessTransaction(TestFieldsUtil.generateTransactionMessageDTOFromWeb(),
                                principal);
        Transaction resultTransactionWithoutPrincipal =
                transactionProcessingService
                        .validateAndProcessTransaction(TestFieldsUtil.generateTransactionMessageDTOFromTelegram(),
                                null);


        assertEquals(resultTransactionWithPrincipal.getMessage(), resultTransactionWithoutPrincipal.getMessage());
        assertEquals(resultTransactionWithPrincipal.getAmount(), resultTransactionWithoutPrincipal.getAmount());
        assertEquals(resultTransactionWithPrincipal.getAccount().getId(),
                resultTransactionWithoutPrincipal.getAccount().getId());
        assertEquals(resultTransactionWithPrincipal.getCategory().getName(),
                resultTransactionWithoutPrincipal.getCategory().getName());
    }
}
