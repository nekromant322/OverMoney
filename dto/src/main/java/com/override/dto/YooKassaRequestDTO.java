package com.override.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.override.dto.constants.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YooKassaRequestDTO {
    private boolean capture = true;
    private String description;
    private Amount amount;
    private Confirmation confirmation;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Amount {
        private String value;
        private Currency currency;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Confirmation {
        @Builder.Default
        private String type = "redirect";

        @JsonProperty("return_url")
        private String returnUrl;
    }
}