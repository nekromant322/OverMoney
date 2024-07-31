package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidDataException extends BaseException {

    public InvalidDataException() {
        super();
    }

    public InvalidDataException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getErrorCode() {
        return "INVALID_DATA";
    }
}
