package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class RequestSizeException extends BaseException {

    public RequestSizeException() {
        super();
    }

    public RequestSizeException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.PAYLOAD_TOO_LARGE.value();
    }

    @Override
    public String getErrorCode() {
        return HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase();
    }
}
