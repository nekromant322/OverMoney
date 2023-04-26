package com.override.orchestrator_service.mapper;

import com.override.orchestrator_service.constants.Type;
import com.override.orchestrator_service.model.Transaction;
import org.springframework.stereotype.Component;

import javax.management.InstanceNotFoundException;

@Component
public class TransactionMapper {

    public String mapTransactionToTelegramMessage(Transaction transaction) throws InstanceNotFoundException {
        String telegramMessage = "Записал в ";
        if (transaction.getCategory().getType() == null) {
            throw new InstanceNotFoundException("No type set for transaction");
        }
        if (transaction.getCategory().getType() == Type.INCOME) {
            telegramMessage += "Доходы";
        }
        if (transaction.getCategory().getType() == Type.EXPENSE) {
            telegramMessage += "Расходы";
        }
        String category = transaction.getCategory().getName();
        String amount = transaction.getAmount().toString();
        String comment = transaction.getMessage();
        telegramMessage += " -> " + category + ". Сумма: " + amount + "р. Примечание: " + comment;
        return telegramMessage;
    }
}
