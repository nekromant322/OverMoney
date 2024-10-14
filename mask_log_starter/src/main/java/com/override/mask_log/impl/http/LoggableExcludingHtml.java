package com.override.mask_log.impl.http;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.zalando.logbook.*;
import org.zalando.logbook.Sink;

public class LoggableExcludingHtml implements Sink {

    private final HttpLogFormatter formatter;
    private final HttpLogWriter writer;

    public LoggableExcludingHtml(HttpLogFormatter formatter, HttpLogWriter writer) {
        this.formatter = formatter;
        this.writer = writer;
    }

    @Override
    public void write(final @NotNull Precorrelation precorrelation, final HttpRequest request)
        throws IOException {
        String requestBody = new String(request.getBody());

        if (shouldLog(requestBody)) {
            final String requestLog = formatter.format(precorrelation, request);
            writer.write(precorrelation, requestLog);
        }
    }

    @Override
    public void write(final @NotNull Correlation correlation, final HttpRequest request,
            final HttpResponse response) throws IOException {

        String responseBody = new String(response.getBody());

        if (shouldLog(responseBody)) {
            final String responseLog = formatter.format(correlation, response);
            writer.write(correlation, responseLog);
        }
    }

    private boolean shouldLog(String body) {
        return !body.contains("<!DOCTYPE html>");
    }
}