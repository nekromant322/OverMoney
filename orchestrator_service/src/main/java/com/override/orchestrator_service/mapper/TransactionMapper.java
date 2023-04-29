package com.override.orchestrator_service.mapper;

import com.override.orchestrator_service.constants.Type;
import com.override.orchestrator_service.model.Transaction;
import org.springframework.stereotype.Component;

import javax.management.InstanceNotFoundException;
import java.util.Objects;

@Component
public class TransactionMapper {
    private final String INCOME = "Доходы";
    private final String EXPENSE = "Расходы";
    private final String CATEGORY_UNDEFINED = "Нераспознанное";

    public String mapTransactionToTelegramMessage(Transaction transaction) throws InstanceNotFoundException {
        String type = getTransactionType(transaction);
        String category = getTransactionCategory(transaction);
        String amount = transaction.getAmount().toString();
        String comment = transaction.getMessage();

        return "Записал в " + type + " -> " + category + ". Сумма: " + amount + " р. Примечание: " + comment;
    }

    private String getTransactionType(Transaction transaction) throws InstanceNotFoundException {
        if (Objects.isNull(transaction.getCategory()) || transaction.getCategory().getType() == Type.EXPENSE) {
            return EXPENSE;
        }
        if (transaction.getCategory().getType() == Type.INCOME) {
            return INCOME;
        }
        throw new InstanceNotFoundException("No type set for transaction");
    }

    private String getTransactionCategory(Transaction transaction) {
        if (Objects.isNull(transaction.getCategory())) {
            return CATEGORY_UNDEFINED;
        }
        return transaction.getCategory().getName();
    }
}
