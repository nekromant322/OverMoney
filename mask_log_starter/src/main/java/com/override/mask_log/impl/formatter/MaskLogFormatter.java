package com.override.mask_log.impl.formatter;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.override.mask_log.config.MaskLogProperties;
import com.override.mask_log.impl.http.MaskedHttpRequest;
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

    private final String MASK_PATTERN = "*****";
    private final int FIRST_HEADER_INDEX = 0;
    private final int SECOND_HEADER_INDEX = 1;

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
            for (String secret : maskLogProperties.getMaskedFields()) {
                String secretValuePattern = "\\\"" + secret + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"";
                String replacementPattern = "\"" + secret + "\"" + ":" + MASK_PATTERN;
                body = body.replaceAll(secretValuePattern, replacementPattern);
            }
            return Optional.of(new MaskLogFormatter.JsonBody(body));
        }
    }


    /**
     * Подготавливает тело HTTP-сообщения для логирования путем маскировки секретных полей
     * в заголовках и URI.
     *
     * @param precorrelation объект Precorrelation, содержащий информацию о предшествующем запросе
     * @param request        объект HttpRequest, представляющий исходящее HTTP-сообщение
     * @return отформатированное для логирования представление исходящего сообщения, с маскировкой секретных полей
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    public String format(@NotNull Precorrelation precorrelation, @NotNull HttpRequest request) throws IOException {
        List<String> secrets = maskLogProperties.getMaskedFields();
        return StructuredHttpLogFormatter.super.format(precorrelation, maskUri(maskHeader(request, secrets)));
    }

    /**
     * Рекурсивный метод maskHeader маскирует любые секретные заголоки HTTP-сообщения и возвращает
     * новый объект HttpRequest с обновленными заголовками.
     * В каждой итерации проверяется первый хедер из списка, обновленный HTTP запрос содержит сублист хедеров
     * исключая первый эелемнет.
     * <p>
     * Сдлеано так сложно т.к. спсиок хедеров не изменяемый обьект, и в запрос можно подсунуть только новый список.
     *
     * @param request       объект HttpRequest, представляющий HTTP-сообщение
     * @param secretHeaders список строк, содержащий названия секретных заголовков
     * @return объект HttpRequest с обновленными заголовками
     */
    private HttpRequest maskHeader(HttpRequest request, List<String> secretHeaders) {
        if (secretHeaders.isEmpty()) {
            return request;
        } else {
            String secretHeader = secretHeaders.get(FIRST_HEADER_INDEX);
            HttpHeaders httpHeaders = request.getHeaders();
            HttpHeaders maskedHeaders = httpHeaders.update(secretHeader, MASK_PATTERN);
            MaskedHttpRequest updatedRequest = new MaskedHttpRequest(request, maskedHeaders, request.getRequestUri());
            List<String> remainingHeaders = secretHeaders.subList(SECOND_HEADER_INDEX, secretHeaders.size());
            return maskHeader(updatedRequest, remainingHeaders);
        }
    }

    private HttpRequest maskUri(HttpRequest request) {
        String query = request.getRequestUri();
        String maskedUri = query.replaceAll("=([^&]*)", MASK_PATTERN);
        return new MaskedHttpRequest(request, request.getHeaders(), maskedUri);
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