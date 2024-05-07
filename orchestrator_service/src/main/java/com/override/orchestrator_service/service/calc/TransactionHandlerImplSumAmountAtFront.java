package com.override.orchestrator_service.service.calc;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TransactionHandlerImplSumAmountAtFront implements TransactionHandler {
    private final String SPACE = " ";
    private final String NOTHING = "";
    private final String PLUS = "\\+";
    private final char RU_DECIMAL_DELIMITER = ',';
    private final char EN_DECIMAL_DELIMITER = '.';
    private final String regexpForAmountAtFront =
            "^(\\d*(\\,|\\.)?\\d+)((\\s*)(\\+((\\s*)\\d*(\\,|\\.)?\\d+)(\\s*)))+\\s+";

    @Override
    public double calculateAmount(String transaction) {
        Pattern pattern = Pattern.compile(regexpForAmountAtFront);
        Matcher matcher = pattern.matcher(transaction);
        matcher.find();
        String amountAsString = matcher.group()
                .trim()
                .replace(RU_DECIMAL_DELIMITER, EN_DECIMAL_DELIMITER)
                .replace(SPACE, NOTHING);
        Stream<String> amountAsStream = Arrays.stream(amountAsString.split(PLUS));
        final double[] sum = {0};
        amountAsStream.forEach(t -> {
            sum[0] += Double.parseDouble(t);
        });
        return sum[0];
    }

    @Override
    public String getTransactionComment(String transaction) {
        Pattern pattern = Pattern.compile(regexpForAmountAtFront);
        Matcher matcher = pattern.matcher(transaction);
        return matcher.replaceAll(NOTHING).trim();
    }

    @Override
    public String getRegExp() {
        return "^(\\d*(\\,|\\.)?\\d+)((\\s*)(\\+((\\s*)\\d*(\\,|\\.)?\\d+)(\\s*)))+\\s+([a-zA-Zа-яА-я0-9\\p{P}\\s]+)";
    }
}
