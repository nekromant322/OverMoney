package com.override.dto.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {
    SUCCESS("success"),
    FAILED("failed"),
    PENDING("pending");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }
}