package com.override.orchestrator_service.service.calc;

public class TransactionHandlerImplSingleAmountAtFront implements TransactionHandler {
    private final String SPACE = " ";
    private final char RU_DECIMAL_DELIMITER = ',';
    private final char EN_DECIMAL_DELIMITER = '.';

    @Override
    public float calculateAmount(String transaction) {
        String amountAsString = transaction.substring(0, transaction.indexOf(SPACE));
        return Float.parseFloat(amountAsString.replace(RU_DECIMAL_DELIMITER, EN_DECIMAL_DELIMITER));
    }

    @Override
    public String getTransactionComment(String transaction) {
        return transaction.substring(transaction.indexOf(SPACE) + 1);
    }

    @Override
    public String getRegExp() {
        return "^(\\d*(\\,|\\.)?\\d+)(\\s+)([a-zA-Zа-яА-я0-9\\p{P}\\s]+)";
    }
}
