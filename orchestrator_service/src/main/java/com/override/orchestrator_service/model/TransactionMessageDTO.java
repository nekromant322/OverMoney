package com.override.orchestrator_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionMessageDTO {
    private String message;
    private String username;
}
