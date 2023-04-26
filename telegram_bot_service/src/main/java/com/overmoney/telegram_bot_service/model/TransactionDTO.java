package com.overmoney.telegram_bot_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDTO {
    private String message;
    private String username;
}
