package com.override.orchestrator_service.mapper;

import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.constants.Type;
import com.override.orchestrator_service.model.Transaction;
import com.override.dto.TransactionResponseDTO;
import org.springframework.stereotype.Component;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {
    private final String INCOME = "Доходы";
    private final String EXPENSE = "Расходы";
    private final String CATEGORY_UNDEFINED = "Нераспознанное";

    public TransactionResponseDTO mapTransactionToTelegramResponse(Transaction transaction) throws InstanceNotFoundException {
        return TransactionResponseDTO.builder()
                .type(getTransactionType(transaction))
                .category(getTransactionCategory(transaction))
                .amount(transaction.getAmount().toString())
                .chatId(transaction.getAccount().getChatId())
                .comment(transaction.getMessage())
                .build();
    }

    public TransactionDTO mapTransactionToJson(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .message(transaction.getMessage())
                .build();
    }

    public List<TransactionDTO> mapTransactionListToJsonList(List<Transaction> transactions) {
        return transactions
                .stream()
                .map(this::mapTransactionToJson)
                .collect(Collectors.toList());
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
