package com.overmoney.telegram_bot_service.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TelegramMessageCheckerServiceTest {

    static TelegramMessageCheckerService messageCheckerService;

    @BeforeAll
    static void setup() {
        messageCheckerService = new TelegramMessageCheckerService();
    }

    @ParameterizedTest
    @MethodSource({"argsForMessageCheckerMethod"})
    void testForNonTransactionalMessageMentionedToSomeoneMethod(String message, boolean expected) {
        assertEquals(expected, messageCheckerService.isNonTransactionalMessageMentionedToSomeone(message));
    }

    static Stream<Arguments> argsForMessageCheckerMethod() {
        return Stream.of(
                Arguments.of("@someone", true),
                Arguments.of("@someone запиши покупки", true),
                Arguments.of("@someone 123 @someone", true),
                Arguments.of("@someone @someone", true),
                Arguments.of("someone", false),
                Arguments.of("@someone запиши покупки 123", false),
                Arguments.of("123 @someone @someone", false),
                Arguments.of("123 123", false),
                Arguments.of("123 someone", false),
                Arguments.of("someone 123", false),
                Arguments.of("123 @someone 123 @someone 123", false),
                Arguments.of("someone 123", false),
                Arguments.of("someone привет", false)
        );
    }

    @Test
    void shouldReturnFalseForEmptyMessage() {
        assertFalse(messageCheckerService.isNonTransactionalMessageMentionedToSomeone(""));
    }
}
