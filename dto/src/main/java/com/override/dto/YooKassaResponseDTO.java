package com.override.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.override.dto.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YooKassaResponseDTO {
    private String id;
    private PaymentStatus status;
    private Confirmation confirmation;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Confirmation {
        @JsonProperty("confirmation_url")
        private String confirmationUrl;
    }
}