package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {
    private UUID id;
    private String categoryName;
    private String message;
    private Float amount;
    private LocalDateTime date;
    private Long suggestedCategoryId;
    private Long telegramUserId;
    private String telegramUserName;
}
