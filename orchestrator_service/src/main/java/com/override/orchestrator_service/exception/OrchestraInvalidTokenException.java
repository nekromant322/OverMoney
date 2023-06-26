package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class OrchestraInvalidTokenException extends BaseException {

    public OrchestraInvalidTokenException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

    @Override
    public String getErrorCode() {
        return "ORCHESTRA_INVALID_TOKEN";
    }
}

