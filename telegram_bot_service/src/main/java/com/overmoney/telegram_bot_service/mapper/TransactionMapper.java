package com.overmoney.telegram_bot_service.mapper;

import com.overmoney.telegram_bot_service.model.TransactionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public String mapTransactionResponseToTelegramMessage(TransactionResponseDTO transactionResponseDTO) {
        return "Записал в " + transactionResponseDTO.getType() +
                " -> " + transactionResponseDTO.getCategory() +
                ". Сумма: " + transactionResponseDTO.getAmount() + " р. " +
                "Примечание: " + transactionResponseDTO.getComment();
    }
}
