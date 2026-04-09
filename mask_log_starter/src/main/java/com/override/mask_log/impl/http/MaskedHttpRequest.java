package com.override.mask_log.impl.http;

import org.jetbrains.annotations.Nullable;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.Origin;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class MaskedHttpRequest implements HttpRequest {

    private HttpRequest httpRequest;
    private HttpHeaders maskedHeaders;
    private String maskedUri;

    public MaskedHttpRequest(HttpRequest httpRequest, HttpHeaders maskedHeaders , String maskedUri) {
        this.httpRequest = httpRequest;
        this.maskedHeaders = maskedHeaders;
        this.maskedUri = maskedUri;
    }

    @Override
    public String getRemote() {
        return httpRequest.getRemote();
    }

    @Override
    public String getMethod() {
        return this.httpRequest.getMethod();
    }

    @Override
    public String getProtocolVersion() {
        return HttpRequest.super.getProtocolVersion();
    }

    @Nullable
    @Override
    public String getContentType() {
        return HttpRequest.super.getContentType();
    }

    @Override
    public Charset getCharset() {
        return HttpRequest.super.getCharset();
    }

    @Override
    public String getBodyAsString() throws IOException {
        return HttpRequest.super.getBodyAsString();
    }

    @Override
    public Origin getOrigin() {
        return httpRequest.getOrigin();
    }

    @Override
    public HttpHeaders getHeaders() {
        return maskedHeaders;
    }

    @Override
    public byte[] getBody() throws IOException {
        return httpRequest.getBody();
    }

    @Override
    public String getRequestUri() {
        return maskedUri;
    }

    @Override
    public String getScheme() {
        return httpRequest.getScheme();
    }

    @Override
    public String getHost() {
        return httpRequest.getHost();
    }

    @Override
    public Optional<Integer> getPort() {
        return httpRequest.getPort();
    }

    @Override
    public String getPath() {
        return httpRequest.getPath();
    }

    @Override
    public String getQuery() {
        return maskedUri;
    }

    @Override
    public HttpRequest withBody() throws IOException {
        return httpRequest.withBody();
    }

    @Override
    public HttpRequest withoutBody() {
        return httpRequest.withoutBody();
    }
}
