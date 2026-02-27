package com.override.orchestrator_service.util;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Generated;
import org.zalando.logbook.HttpMessage;
import org.zalando.logbook.StructuredHttpLogFormatter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MyJsonLogFormatter implements StructuredHttpLogFormatter {

    private final List<String> REPLACE_SECRETS_LIST = Arrays.asList("token", "accountId", "x-internal-key");

    public Optional<Object> prepareBody(final HttpMessage message) throws IOException {

        String body = message.getBodyAsString();

        if (body.isEmpty()) {
            return Optional.empty();
        } else {
            for (String secret : REPLACE_SECRETS_LIST) {
                String secretValuePattern = "\\\"" + secret + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"";
                String replacementPattern = "\"" + secret + "\"" + ":\"***\"";
                body = body.replaceAll(secretValuePattern, replacementPattern);
            }
            return Optional.of(new MyJsonLogFormatter.JsonBody(body));
        }
    }

    public String format(final Map<String, Object> content) throws IOException {
        return this.mapper.writeValueAsString(content);
    }

    private final ObjectMapper mapper;

    public MyJsonLogFormatter() {
        this(new ObjectMapper());
    }

    public MyJsonLogFormatter(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private static final class JsonBody {
        String json;

        @JsonRawValue
        @JsonValue
        String getJson() {
            return this.json;
        }

        @Generated
        JsonBody(final String json) {
            this.json = json;
        }
    }
}
