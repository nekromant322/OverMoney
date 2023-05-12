package com.override.orchestrator_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {
    private String type;
    private String category;
    private String amount;
    private String comment;
    private UUID userId;
}
