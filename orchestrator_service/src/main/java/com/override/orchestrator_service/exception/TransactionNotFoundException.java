package com.override.orchestrator_service.exception;

import org.springframework.data.crossstore.ChangeSetPersister;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException() {
        super();
    }
}