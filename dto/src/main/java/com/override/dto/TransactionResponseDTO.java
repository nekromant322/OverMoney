package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponseDTO {
    private UUID id;
    private String type;
    private String category;
    private String amount;
    private String comment;
    private Long chatId;
}
