package com.override.dto.constants;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    SUCCESS("success"),
    FAILED("failed"),
    PENDING("pending");

    @JsonValue
    private final String status;
}