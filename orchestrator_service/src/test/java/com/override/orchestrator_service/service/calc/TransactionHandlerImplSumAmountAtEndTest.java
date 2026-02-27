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
public class TransactionHandlerImplSumAmountAtEndTest {
    @InjectMocks
    private TransactionHandlerImplSumAmountAtEnd handler;

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
                Arguments.of("пиво теплое! 1.5 200  + 200,1+200.1+200", "пиво теплое! 1.5", 800.2f, "продукты"),
                Arguments.of("пиво теплое! 1.5 200+   200,12  +   200.13  +200", "пиво теплое! 1.5", 800.25f, "продукты"),
                Arguments.of("пиво теплое! 1,5 200+   200,12  +   200.13  +200", "пиво теплое! 1,5", 800.25f, "продукты"),
                Arguments.of("мёд 100  + 200 +300", "мёд", 600f, "продукты"),
                Arguments.of("ёршик 100 +  200 +  300 +  400", "ёршик", 1000f, null),
                Arguments.of("мёд 100+200+300", "мёд", 600f, "продукты"),
                Arguments.of("мёд 100    +    200 +   300", "мёд", 600f, "продукты"),
                Arguments.of("мёд 100", "мёд", 100f, "продукты"),
                Arguments.of("мёд 50 +  50", "мёд", 100f, "продукты")
        );
    }
}
