package com.override.orchestrator_service.service.calc;

public interface TransactionHandler {
    double calculateAmount(String transaction);

    String getTransactionComment(String transaction);

    String getRegExp();
}