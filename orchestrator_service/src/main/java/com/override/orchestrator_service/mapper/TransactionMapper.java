package com.override.orchestrator_service.mapper;

import com.override.dto.*;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.util.NumericalUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.management.InstanceNotFoundException;
import java.util.*;

@Component
public class TransactionMapper {
    private final String INCOME = "Доходы";
    private final String EXPENSE = "Расходы";
    private final String CATEGORY_UNDEFINED = "Нераспознанное";

    @Value("${recognizer.min-accuracy}")
    private double minAccuracy;

    public TransactionResponseDTO mapTransactionToTelegramResponse(Transaction transaction)
            throws InstanceNotFoundException {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .type(getTransactionType(transaction))
                .category(getTransactionCategory(transaction))
                .amount(NumericalUtils.roundAmount(transaction.getAmount()).toString())
                .chatId(transaction.getAccount().getChatId())
                .comment(transaction.getMessage())
                .build();
    }

    public TransactionResponseDTO mapTransactionToTelegramResponse(Transaction transaction, UUID bindingUUID)
            throws InstanceNotFoundException {
        TransactionResponseDTO dto = mapTransactionToTelegramResponse(transaction);
        dto.setBindingUuid(bindingUUID);
        return dto;
    }

    public TransactionDTO mapTransactionToDTO(Transaction transaction) {
        TransactionDTO.TransactionDTOBuilder builder = TransactionDTO.builder()
                .id(transaction.getId())
                .amount(NumericalUtils.roundAmount(transaction.getAmount()))
                .message(transaction.getMessage())
                .date(transaction.getDate())
                .telegramUserId(transaction.getTelegramUserId());
        if (transaction.getCategory() != null) {
            builder.categoryName(transaction.getCategory().getName());
            builder.type(transaction.getCategory().getType());
        }
        Suggestion suggestion = transaction.getSuggestion();
        if (suggestion != null && suggestion.getAccuracy() != null) {
            builder.accuracy(suggestion.getAccuracy());
        }
        if (suggestion != null && suggestion.getAccuracy() != null && suggestion.getAccuracy() >= minAccuracy) {
            builder.suggestedCategoryId(suggestion.getSuggestedCategoryId());
        }
        if (transaction.getCategory() != null) {
            builder.categoryName(transaction.getCategory().getName());
            builder.type(transaction.getCategory().getType());
        }

        return builder.build();
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