package com.override.orchestrator_service.service.calc;

public interface TransactionHandler {
    float calculateAmount(String transaction);
    String getTransactionComment(String transaction);
}