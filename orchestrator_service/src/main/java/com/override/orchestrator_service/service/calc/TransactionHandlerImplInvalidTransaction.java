package com.override.orchestrator_service.service.calc;

import com.override.orchestrator_service.exception.TransactionProcessingException;

public class TransactionHandlerImplInvalidTransaction implements TransactionHandler {
    @Override
    public float calculateAmount(String transaction) {
        throw new TransactionProcessingException("Неподдерживаемый формат транзакции");
    }

    @Override
    public String getTransactionComment(String transaction) {
        throw new TransactionProcessingException("Неподдерживаемый формат транзакции");
    }

    @Override
    public String getRegExp() {
        return ".*";
    }
}
