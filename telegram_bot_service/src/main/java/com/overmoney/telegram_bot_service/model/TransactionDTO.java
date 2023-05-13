package com.overmoney.telegram_bot_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private String message;
    private String username;
}
