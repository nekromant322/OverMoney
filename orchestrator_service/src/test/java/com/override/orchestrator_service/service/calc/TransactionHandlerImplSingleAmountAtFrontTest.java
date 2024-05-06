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
public class TransactionHandlerImplSingleAmountAtFrontTest {
    @InjectMocks
    private TransactionHandlerImplSingleAmountAtFront handler;

    @ParameterizedTest
    @MethodSource("provideTransactions")
    public void calculateAmountWorksCorrectTest(String message, String unusedField, double expectedResult) {
        assertEquals(handler.calculateAmount(message), expectedResult, 0.0001d);
    }

    @ParameterizedTest
    @MethodSource("provideTransactions")
    public void getTransactionCommentWorksCorrectTest(String message, String expectedResult) {
        assertEquals(handler.getTransactionComment(message), expectedResult);
    }

    private static Stream<Arguments> provideTransactions() {
        return Stream.of(
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
                Arguments.of("500 пиво теплое! 1.5", "пиво теплое! 1.5", 500f, "продукты")
        );
    }
}
