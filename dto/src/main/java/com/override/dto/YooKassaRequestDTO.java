package com.override.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class YooKassaRequestDTO {
    private boolean capture = true;
    private String description;
    private Amount amount;
    private Confirmation confirmation;

    @Data
    public static class Amount {
        private String value;
        private String currency;
    }

    @Data
    public static class Confirmation {
        private String type = "redirect";

        @JsonProperty("return_url")
        private String returnUrl;
    }
}