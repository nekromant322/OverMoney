package com.override.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class YooKassaResponseDTO {
    private String id;
    private String status;
    private Confirmation confirmation;

    @Data
    public static class Confirmation {
        @JsonProperty("confirmation_url")
        private String confirmationUrl;
    }
}