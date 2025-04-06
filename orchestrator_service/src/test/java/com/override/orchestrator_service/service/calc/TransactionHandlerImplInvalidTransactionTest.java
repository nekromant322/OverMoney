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
                Arguments.of("мёд"),
                Arguments.of("мёд 200 мёд"),
                Arguments.of("мёд200"),
                Arguments.of("200мёд"),
                Arguments.of("мёд200 мёд"),
                Arguments.of("мёд мёд200"),
                Arguments.of("200мёд мёд"),
                Arguments.of("мёд 200мёд"),

                Arguments.of("200мёд 200 мёд"),
                Arguments.of("200.5мёд 200.5 мёд"),
                Arguments.of("200,5мёд 200,5 мёд"),

                Arguments.of("мёд 200 мёд200"),
                Arguments.of("мёд 200.5 мёд200.5"),
                Arguments.of("мёд 200,5 мёд200,5"),

                Arguments.of("200мёд 200 мёд200"),
                Arguments.of("200.5мёд 200.5 мёд200.5"),
                Arguments.of("200,5мёд 200,5 мёд200,5"),

                Arguments.of("100+100"),
                Arguments.of("мёд 100+1-1"),
                Arguments.of("мёд 100+1/1"),
                Arguments.of("мёд 100+1*1"),
                Arguments.of("мёд 100+1-1"),
                Arguments.of("200+один+200 мёд"),
                Arguments.of("200+1 +один мёд"),

                Arguments.of("мёд 200"),
                Arguments.of("мёд .45"),
                Arguments.of("мёд ,45"),
                Arguments.of("мёд 1.5 .45"),
                Arguments.of("мёд 1.5 ,45"),
                Arguments.of("мёд 777 100"),
                Arguments.of("мёд 777 123.45"),
                Arguments.of("мёд 777 123,45"),
                Arguments.of("мёд7 200"),
                Arguments.of("мёд7 123.45"),
                Arguments.of("мёд7 123,45"),
                Arguments.of("7мёд 100"),
                Arguments.of("продукты 200"),
                Arguments.of("мёд! 100"),
                Arguments.of("мёд теплый 200"),
                Arguments.of("мёд 777 теплый 200"),
                Arguments.of("мёд 777 ! теплый 200"),
                Arguments.of("мёд теплый 123.45"),
                Arguments.of("мёд теплый 123,45"),
                Arguments.of("мёд теплый 123.45"),
                Arguments.of("мёд теплый 123,45"),
                Arguments.of("мёд теплый 500"),
                Arguments.of("мёд теплый 777 500"),
                Arguments.of("мёд теплый 1.5 500"),
                Arguments.of("мёд теплый 1.5 500.23"),
                Arguments.of("мёд теплый 1.5 500,23"),
                Arguments.of("мёд теплый! 1.5 500"),
                Arguments.of("мёд теплый! 1.5 500.23"),
                Arguments.of("мёд теплый! 1.5 500,23"),

                Arguments.of("200 мёд"),
                Arguments.of(".45 мёд"),
                Arguments.of(",45 мёд"),
                Arguments.of("1.5 .45 мёд"),
                Arguments.of("1,5 .45 мёд"),
                Arguments.of("100 мёд 777"),
                Arguments.of("123.45 мёд 777"),
                Arguments.of("123,45 мёд 777"),
                Arguments.of("200 мёд7"),
                Arguments.of("123.45 мёд7"),
                Arguments.of("123,45 мёд7"),
                Arguments.of("100 7мёд"),
                Arguments.of("продукты 200"),
                Arguments.of("100 мёд!"),
                Arguments.of("200 мёд теплый"),
                Arguments.of("200 мёд 777 теплый"),
                Arguments.of("200 мёд 777 ! теплый"),
                Arguments.of("123.45 мёд теплый"),
                Arguments.of("123,45 мёд теплый"),
                Arguments.of("123.45 мёд теплый"),
                Arguments.of("123,45 мёд теплый"),
                Arguments.of("500 мёд теплый"),
                Arguments.of("500 мёд теплый 777"),
                Arguments.of("500 мёд теплый 1.5"),
                Arguments.of("500 мёд теплый! 1.5"),

                Arguments.of("200+200 мёд теплый"),
                Arguments.of("мёд теплый 200+200"),
                Arguments.of("200.1+200.1 мёд теплый+777"),
                Arguments.of("200,1+200.1 мёд теплый+1+1"),
                Arguments.of("200,1+200.1 мёд теплый 1 +1"),
                Arguments.of("200,1+200.1+200.12 мёд", "мёд"),
                Arguments.of("мёд 200,1 +200.1+ 200.12"),
                Arguments.of("200+200,1+200.1+200 мёд теплый! 1.5"),
                Arguments.of("мёд теплый! 1.5 200  + 200,1+200.1+200"),
                Arguments.of("мёд теплый! 1.5 200+   200,12  +   200.13  +200"),
                Arguments.of("мёд теплый! 1,5 200+   200,12  +   200.13  +200")
        );
    }

}
