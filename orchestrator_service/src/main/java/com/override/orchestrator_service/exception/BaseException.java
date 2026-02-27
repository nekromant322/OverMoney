package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public String getErrorCode() {
        return "ORCHESTRA_SERVICE_UNEXPECTED";
    }
}
