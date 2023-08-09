package com.override.mask_log.formatter;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.override.mask_log.config.MaskLogProperties;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.logbook.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MaskLogFormatter implements StructuredHttpLogFormatter {

    @Autowired
    private MaskLogProperties maskLogProperties;

    private final ObjectMapper mapper;

    public MaskLogFormatter() {
        this(new ObjectMapper());
    }

    public MaskLogFormatter(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Подготавливает тело HTTP-сообщения для логирования путем маскировки любых секретных полей
     * в передаваемом JSON.
     *
     * @param message HTTP-сообщение
     * @return объект Optional, содержащий маскированное тело, или пустой Optional, если тело пустое
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public Optional<Object> prepareBody(final HttpMessage message) throws IOException {
        String body = message.getBodyAsString();
        if (body.isEmpty()) {
            return Optional.empty();
        } else {
            for (String secret : maskLogProperties.getSecretFields()) {
                String secretValuePattern = "\\\"" + secret + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"";
                String replacementPattern = "\"" + secret + "\"" + ":\"***\"";
                body = body.replaceAll(secretValuePattern, replacementPattern);
            }
            return Optional.of(new MaskLogFormatter.JsonBody(body));
        }
    }



    /**
     * Подготавливает тело HTTP-сообщения для логирования путем маскировки секретных полей
     * в заголовках и URI.
     *
     * @param precorrelation объект Precorrelation, содержащий информацию о предшествующей связи
     * @param request объект HttpRequest, представляющий HTTP-сообщение
     * @return объект Optional, содержащий маскированное тело сообщения, или пустой Optional, если тело пустое
     */
    @Override
    public String format(@NotNull Precorrelation precorrelation, @NotNull HttpRequest request) throws IOException {
        List<String> secrets = maskLogProperties.getSecretFields();
        HttpRequest maskedHeaderAndQueryRequest = maskHeaderAndQuery(request, secrets);
        return StructuredHttpLogFormatter.super.format(precorrelation, maskedHeaderAndQueryRequest);
    }

    private HttpRequest maskHeaderAndQuery(HttpRequest httpRequest, List<String> secrets) {
        return maskHeader(maskQuery(httpRequest), secrets);
    }

    private HttpRequest maskQuery(HttpRequest request) {
        String query = request.getQuery();
        String maskedQuery = query.replaceAll("=([^&]*)", "=****");
        HttpRequest updatedRequest = new HttpRequest() {
            @Override
            public String getRemote() {
                return request.getRemote();
            }

            @Override
            public String getMethod() {
                return request.getMethod();
            }

            @Override
            public String getScheme() {
                return request.getScheme();
            }

            @Override
            public String getHost() {
                return request.getHost();
            }

            @Override
            public Optional<Integer> getPort() {
                return request.getPort();
            }

            @Override
            public String getPath() {
                return request.getPath();
            }

            @Override
            public String getQuery() {
                return maskedQuery;
            }

            @Override
            public HttpRequest withBody() throws IOException {
                return request.withBody();
            }

            @Override
            public HttpRequest withoutBody() {
                return request.withoutBody();
            }

            @Override
            public Origin getOrigin() {
                return request.getOrigin();
            }

            @Override
            public HttpHeaders getHeaders() {
                return request.getHeaders();
            }

            @Override
            public byte[] getBody() throws IOException {
                return request.getBody();
            }
        };
        return updatedRequest;
    }

    private HttpRequest maskHeader(HttpRequest request, List<String> secretHeaders) {
        if (secretHeaders.isEmpty()) {
            return request;
        } else {
            String secretHeader = secretHeaders.get(0);
            HttpHeaders httpHeaders = request.getHeaders();
            HttpHeaders maskedHeaders = httpHeaders.update(secretHeader, "*****");
            HttpRequest updatedRequest = new HttpRequest() {
                @Override
                public String getRemote() {
                    return request.getRemote();
                }

                @Override
                public String getMethod() {
                    return request.getMethod();
                }

                @Override
                public String getScheme() {
                    return request.getScheme();
                }

                @Override
                public String getHost() {
                    return request.getHost();
                }

                @Override
                public Optional<Integer> getPort() {
                    return request.getPort();
                }

                @Override
                public String getPath() {
                    return request.getPath();
                }

                @Override
                public String getQuery() {
                    return request.getQuery();
                }

                @Override
                public HttpRequest withBody() throws IOException {
                    return request.withBody();
                }

                @Override
                public HttpRequest withoutBody() {
                    return request.withoutBody();
                }

                @Override
                public Origin getOrigin() {
                    return request.getOrigin();
                }

                @Override
                public HttpHeaders getHeaders() {
                    return maskedHeaders;
                }

                @Override
                public byte[] getBody() throws IOException {
                    return request.getBody();
                }
            };
            List<String> remainingHeaders = secretHeaders.subList(1, secretHeaders.size());
            return maskHeader(updatedRequest, remainingHeaders);
        }
    }

    public String format(final Map<String, Object> content) throws IOException {
        return this.mapper.writeValueAsString(content);
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