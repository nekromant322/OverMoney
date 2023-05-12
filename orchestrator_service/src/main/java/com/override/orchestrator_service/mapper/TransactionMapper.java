package com.override.orchestrator_service.mapper;

import com.override.orchestrator_service.constants.Type;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.model.TransactionResponseDTO;
import org.springframework.stereotype.Component;

import javax.management.InstanceNotFoundException;
import java.util.Objects;

@Component
public class TransactionMapper {
    private final String INCOME = "Доходы";
    private final String EXPENSE = "Расходы";
    private final String CATEGORY_UNDEFINED = "Нераспознанное";

    public TransactionResponseDTO mapTransactionToTelegramResponse(Transaction transaction) throws InstanceNotFoundException {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        transactionResponseDTO.setType(getTransactionType(transaction));
        transactionResponseDTO.setCategory(getTransactionCategory(transaction));
        transactionResponseDTO.setAmount(transaction.getAmount().toString());
        transactionResponseDTO.setComment(transaction.getMessage());
        transactionResponseDTO.setUserId(transaction.getUser().getUuid());

        return transactionResponseDTO;
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
