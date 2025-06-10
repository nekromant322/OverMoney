package com.override.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.override.dto.constants.PaymentStatus;
import lombok.Data;

@Data
public class YooKassaResponseDTO {
    private String id;
    private PaymentStatus status;
    private Confirmation confirmation;

    @Data
    public static class Confirmation {
        @JsonProperty("confirmation_url")
        private String confirmationUrl;
    }
}