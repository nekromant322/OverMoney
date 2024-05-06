package com.override.orchestrator_service.service.calc;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TransactionHandlerImplSingleAmountAtEndTest {
    @InjectMocks
    private TransactionHandlerImplSingleAmountAtEnd handler;

    @ParameterizedTest
    @MethodSource("provideTransactions")
    public void calculateAmountWorksCorrectTest(String message, String unusedField, double expectedResult) {
        assertEquals(handler.calculateAmount(message), expectedResult, 0.0001f);
    }

    @ParameterizedTest
    @MethodSource("provideTransactions")
    public void getTransactionCommentWorksCorrectTest(String message, String expectedResult) {
        assertEquals(handler.getTransactionComment(message), expectedResult);
    }

    private static Stream<Arguments> provideTransactions() {
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
                Arguments.of("пиво! 100", "пиво!", 100, null),
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
                Arguments.of("пиво теплое! 1.5 500,23", "пиво теплое! 1.5", 500.23, "продукты")
        );
    }
}
