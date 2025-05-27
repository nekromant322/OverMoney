package com.override.recognizer_service.llm.deepseek;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeepSeekBalanceResponseDTO {

    @JsonProperty("is_available")
    private boolean isAvailable;

    @JsonProperty("balance_infos")
    private List<BalanceInfo> balanceInfos;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BalanceInfo {

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("total_balance")
        private String totalBalance;

        @JsonProperty("granted_balance")
        private String grantedBalance;

        @JsonProperty("topped_up_balance")
        private String toppedUpBalance;
    }
}
