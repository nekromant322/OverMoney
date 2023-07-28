package com.override.orchestrator_service.service.calc;

import com.override.orchestrator_service.exception.TransactionProcessingException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TransactionHandlerImplInvalidTransactionTest {
    @InjectMocks
    private TransactionHandlerImplInvalidTransaction handler;

    @ParameterizedTest
    @MethodSource("provideTransactions")
    public void calculateAmountThrowsExceptionTest(String message) {
        assertThrows(TransactionProcessingException.class, () ->
                handler.calculateAmount(message));
    }

    @ParameterizedTest
    @MethodSource("provideTransactions")
    public void getTransactionCommentThrowsExceptionTest(String message) {
        assertThrows(TransactionProcessingException.class, () ->
                handler.getTransactionComment(message));
    }

    private static Stream<Arguments> provideTransactions() {
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
                Arguments.of("200+1 +один пиво"),

                Arguments.of("пиво 200"),
                Arguments.of("пиво .45"),
                Arguments.of("пиво ,45"),
                Arguments.of("пиво 1.5 .45"),
                Arguments.of("пиво 1.5 ,45"),
                Arguments.of("пиво 777 100"),
                Arguments.of("пиво 777 123.45"),
                Arguments.of("пиво 777 123,45"),
                Arguments.of("пиво7 200"),
                Arguments.of("пиво7 123.45"),
                Arguments.of("пиво7 123,45"),
                Arguments.of("7пиво 100"),
                Arguments.of("продукты 200"),
                Arguments.of("пиво! 100"),
                Arguments.of("пиво теплое 200"),
                Arguments.of("пиво 777 теплое 200"),
                Arguments.of("пиво 777 ! теплое 200"),
                Arguments.of("пиво теплое 123.45"),
                Arguments.of("пиво теплое 123,45"),
                Arguments.of("пиво теплое 123.45"),
                Arguments.of("пиво теплое 123,45"),
                Arguments.of("пиво теплое 500"),
                Arguments.of("пиво теплое 777 500"),
                Arguments.of("пиво теплое 1.5 500"),
                Arguments.of("пиво теплое 1.5 500.23"),
                Arguments.of("пиво теплое 1.5 500,23"),
                Arguments.of("пиво теплое! 1.5 500"),
                Arguments.of("пиво теплое! 1.5 500.23"),
                Arguments.of("пиво теплое! 1.5 500,23"),

                Arguments.of("200 пиво"),
                Arguments.of(".45 пиво"),
                Arguments.of(",45 пиво"),
                Arguments.of("1.5 .45 пиво"),
                Arguments.of("1,5 .45 пиво"),
                Arguments.of("100 пиво 777"),
                Arguments.of("123.45 пиво 777"),
                Arguments.of("123,45 пиво 777"),
                Arguments.of("200 пиво7"),
                Arguments.of("123.45 пиво7"),
                Arguments.of("123,45 пиво7"),
                Arguments.of("100 7пиво"),
                Arguments.of("продукты 200"),
                Arguments.of("100 пиво!"),
                Arguments.of("200 пиво теплое"),
                Arguments.of("200 пиво 777 теплое"),
                Arguments.of("200 пиво 777 ! теплое"),
                Arguments.of("123.45 пиво теплое"),
                Arguments.of("123,45 пиво теплое"),
                Arguments.of("123.45 пиво теплое"),
                Arguments.of("123,45 пиво теплое"),
                Arguments.of("500 пиво теплое"),
                Arguments.of("500 пиво теплое 777"),
                Arguments.of("500 пиво теплое 1.5"),
                Arguments.of("500 пиво теплое! 1.5"),

                Arguments.of("200+200 пиво теплое"),
                Arguments.of("пиво теплое 200+200"),
                Arguments.of("200.1+200.1 пиво теплое+777"),
                Arguments.of("200,1+200.1 пиво теплое+1+1"),
                Arguments.of("200,1+200.1 пиво теплое 1 +1"),
                Arguments.of("200,1+200.1+200.12 пиво", "пиво"),
                Arguments.of("пиво 200,1 +200.1+ 200.12"),
                Arguments.of("200+200,1+200.1+200 пиво теплое! 1.5"),
                Arguments.of("пиво теплое! 1.5 200  + 200,1+200.1+200"),
                Arguments.of("пиво теплое! 1.5 200+   200,12  +   200.13  +200"),
                Arguments.of("пиво теплое! 1,5 200+   200,12  +   200.13  +200")
        );
    }

}
