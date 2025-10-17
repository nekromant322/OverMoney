package com.override.dto;

import lombok.Data;

import java.util.Map;

@Data
public class YooKassaWebhookDTO {
    private String event;
    private YooKassaPaymentObjectDTO object;

    @Data
    public static class YooKassaPaymentObjectDTO {
        private String id;
        private String status;
        private YooKassaRequestDTO.Amount amount;
        private String description;
        private Map<String, Object> metadata;
    }
}