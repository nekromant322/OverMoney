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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    @Override
    public Optional<Object> prepareBody(final HttpMessage message) throws IOException {
        String body = message.getBodyAsString();
        if (body.isEmpty()) {
            return Optional.empty();
        }
        String maskedBody = mask(body, "\"([^\"]*)\"(\\s*:\\s*)\"([^\"]*)\"", 1,
                matcher -> "\"$1\"$2\"" + maskString(matcher.group(3)) + "\"");
        return Optional.of(new MaskLogFormatter.JsonBody(maskedBody == null ? body : maskedBody));
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
        return StructuredHttpLogFormatter.super.format(precorrelation, maskUri(maskHeader(request)));
    }

    /**
     * Маскирует секретные заголоки HTTP-сообщения и возвращает
     * новый объект HttpRequest с обновленными заголовками.
     *
     * @param request       объект HttpRequest, представляющий HTTP-сообщение
     * @return объект HttpRequest с обновленными заголовками
     */
    private HttpRequest maskHeader(HttpRequest request) {
        List<String> maskedFields = maskLogProperties.getMaskedFields();
        if (maskedFields.isEmpty()) {
            return request;
        }
        HttpHeaders maskedHeaders = request.getHeaders().apply(maskedFields,
                (header, values) -> values == null ? null
                        : values.stream().map(this::maskString).collect(Collectors.toList()));
        return new MaskedHttpRequest(request, maskedHeaders, request.getRequestUri());
    }

    /** Маскирует секретные query параметры в uri запроса */
    private HttpRequest maskUri(HttpRequest request) {
        String uri = request.getRequestUri();
        String maskedUri = mask(uri, "([?|&])([^=]+)=([^&]+)", 2,
                matcher -> "$1$2=" + maskString(matcher.group(3)));
        return maskedUri == null ? request
                : new MaskedHttpRequest(request, request.getHeaders(), maskedUri);
    }

    public String format(final Map<String, Object> content) throws IOException {
        return this.mapper.writeValueAsString(content);
    }

    /**
     * Находит {@link MaskLogProperties#getMaskedFields() чувствительные} поля в строке по регулярке
     * и заменяет их строкой из функции.
     * @param string строка
     * @param regex {@link Pattern регулярка}
     * @param keyGroup номер {@link Matcher#group(int) группы} в регулярке, которая является ключем в паре,
     *                 значение которой нужно маскировать
     * @param replacementGenerator значение регулярки, будет полностью заменено на строку
     *                             из этой функции, функция работает как
     *                             {@link Matcher#appendReplacement(StringBuffer, String) replacement}
     *
     * @return {@code null} если в строке нет ни одного совпадения, или отформатированную строку.
     */
    private String mask(String string, String regex, int keyGroup, Function<Matcher, String> replacementGenerator) {
        List<String> maskedFields = maskLogProperties.getMaskedFields();
        if (maskedFields.isEmpty()) {
            return null;
        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        boolean result = m.find();
        if (!result) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        do {
            if (maskedFields.contains(m.group(keyGroup))) {
                m.appendReplacement(sb, replacementGenerator.apply(m));
            }
            result = m.find();
        } while (result);
        return m.appendTail(sb).toString();
    }

    private String maskString(String string) {
        int maskPercentage = maskLogProperties.getMaskPercentage();
        String mask = maskLogProperties.getMask();
        return maskString(string, maskPercentage, mask);
    }

    /**
     * Маскирует середину строки в соответствии с параметрами
     * @param string строка
     * @param maskPercentage кол-во скрытых символов в процентах
     * @param mask маска на место скрытых символов
     * @return отформатированную строку
     */
    private String maskString(String string, int maskPercentage, String mask) {
        int maskedCharacters = (int) Math.ceil((string.length() / 100d) * maskPercentage);
        int unmaskedCharacters = string.length() - maskedCharacters;
        return string.substring(0, unmaskedCharacters / 2) +
               mask +
               string.substring(string.length() - unmaskedCharacters / 2);
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