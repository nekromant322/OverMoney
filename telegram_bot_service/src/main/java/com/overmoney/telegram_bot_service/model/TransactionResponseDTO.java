package com.overmoney.telegram_bot_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponseDTO {
    private String type;
    private String category;
    private String amount;
    private String comment;
    private Long userId;
}
