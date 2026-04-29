package com.override.orchestrator_service.event;

import lombok.Value;

import java.util.UUID;

@Value
public class CategoryRecognitionEvent {
    UUID transactionId;
    String message;
    Long telegramUserId;
}
