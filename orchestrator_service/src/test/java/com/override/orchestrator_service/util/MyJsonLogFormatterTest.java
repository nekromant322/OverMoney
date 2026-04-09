package com.override.orchestrator_service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.zalando.logbook.HttpMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MyJsonLogFormatterTest {

    private final MyJsonLogFormatter formatter = new MyJsonLogFormatter();

    @Test
    void testPrepareBodyWithEmptyBody() throws IOException {
        HttpMessage message = mock(HttpMessage.class);
        when(message.getBodyAsString()).thenReturn("");

        Optional<Object> result = formatter.prepareBody(message);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFormat() throws IOException {
        Map<String, Object> content = new HashMap<>();
        content.put("key", "value");

        String result = formatter.format(content);
        assertEquals("{\"key\":\"value\"}", result);
    }

    @Test
    void testConstructorWithCustomMapper() {
        ObjectMapper customMapper = new ObjectMapper();
        MyJsonLogFormatter customFormatter = new MyJsonLogFormatter(customMapper);
        assertNotNull(customFormatter);
    }
}