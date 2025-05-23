package com.override.orchestrator_service.service.calc;

public class TransactionHandlerImplSingleAmountAtEnd implements TransactionHandler {
    private final String SPACE = " ";
    private final char RU_DECIMAL_DELIMITER = ',';
    private final char EN_DECIMAL_DELIMITER = '.';

    @Override
    public double calculateAmount(String transaction) {
        String amountAsString = transaction.substring(transaction.lastIndexOf(SPACE) + 1);
        return Double.parseDouble(amountAsString.replace(RU_DECIMAL_DELIMITER, EN_DECIMAL_DELIMITER));
    }

    @Override
    public String getTransactionComment(String transaction) {
        return transaction.substring(0, transaction.lastIndexOf(SPACE)).trim();
    }

    @Override
    public String getRegExp() {
        return "^([a-zA-Zа-яёА-ЯЁ0-9\\p{P}\\s]+)(\\s+)(\\d*(\\,|\\.)?\\d+)$";
    }
}
