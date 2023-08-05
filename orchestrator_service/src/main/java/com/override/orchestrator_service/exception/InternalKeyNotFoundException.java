package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class InternalKeyNotFoundException extends BaseException {

    public InternalKeyNotFoundException() {
        super();
    }

    public InternalKeyNotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }

    @Override
    public String getErrorCode() {
        return "ORCHESTRA_ACCESS_ERROR";
    }
}
