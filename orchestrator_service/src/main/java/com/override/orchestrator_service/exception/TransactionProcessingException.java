package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class TransactionProcessingException extends BaseException{
    public TransactionProcessingException() {
        super();
    }

    public TransactionProcessingException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getErrorCode() {
        return "ORCHESTRA_TRANSACTION_PROCESSING_FAILED";
    }
}
