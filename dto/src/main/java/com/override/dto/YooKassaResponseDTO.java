package com.override.dto;

import lombok.Data;

@Data
public class YooKassaResponseDTO {
    private String id;
    private String status;
    private Confirmation confirmation;

    @Data
    public static class Confirmation {
        private String confirmationUrl;
    }
}