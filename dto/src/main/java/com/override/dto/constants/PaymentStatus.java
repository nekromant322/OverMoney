package com.override.dto.constants;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    SUCCESS("succeeded"),
    CANCELED("canceled"),
    PENDING("pending"),
    WAITING("waiting_for_capture"),
    ERROR("error");

    @JsonValue
    private final String status;
}