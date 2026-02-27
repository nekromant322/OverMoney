package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class TransactionNotFoundException extends BaseException {
    public TransactionNotFoundException() {
        super();
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }

    @Override
    public String getErrorCode() {
        return "ORCHESTRA_TRANSACTION_NOT_FOUND";
    }
}