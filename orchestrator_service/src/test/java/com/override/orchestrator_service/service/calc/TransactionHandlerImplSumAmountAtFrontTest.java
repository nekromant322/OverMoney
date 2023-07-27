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
public class TransactionHandlerImplSumAmountAtFrontTest {
    @InjectMocks
    private TransactionHandlerImplSumAmountAtFront handler;

    @ParameterizedTest
    @MethodSource("provideTransactions")
    public void calculateAmountWorksCorrectTest(String message, String unusedField, float expectedResult) {
        assertEquals(handler.calculateAmount(message), expectedResult);
    }

    @ParameterizedTest
    @MethodSource("provideTransactions")
    public void getTransactionCommentWorksCorrectTest(String message, String expectedResult) {
        assertEquals(handler.getTransactionComment(message), expectedResult);
    }

    private static Stream<Arguments> provideTransactions() {
        return Stream.of(
                Arguments.of("200+200 пиво теплое", "пиво теплое", 400f, "продукты"),
                Arguments.of("200.1+200.1 пиво теплое+777", "пиво теплое+777", 400.2f, "продукты"),
                Arguments.of("200,1+200.1 пиво теплое+1+1", "пиво теплое+1+1", 400.2f, "продукты"),
                Arguments.of("200,1+200.1 пиво теплое 1 +1", "пиво теплое 1 +1", 400.2f, "продукты"),
                Arguments.of("200,1+200.1+200.12 пиво", "пиво", 600.32f, "продукты")
        );
    }
}
