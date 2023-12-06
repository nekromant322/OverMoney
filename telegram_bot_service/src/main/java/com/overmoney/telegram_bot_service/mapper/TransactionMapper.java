package com.overmoney.telegram_bot_service.mapper;

import com.override.dto.TransactionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public String mapTransactionResponseToTelegramMessage(TransactionResponseDTO transactionResponseDTO) {
        return "Записал в " + transactionResponseDTO.getType() +
                " -> " + transactionResponseDTO.getCategory() +
                ". Сумма: " + transactionResponseDTO.getAmount() +
                " Примечание: " + transactionResponseDTO.getComment();
    }
}
