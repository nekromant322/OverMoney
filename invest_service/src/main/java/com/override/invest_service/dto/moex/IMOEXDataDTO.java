package com.override.invest_service.dto.moex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IMOEXDataDTO {
    private Analytics analytics;

    @JsonProperty("analytics.dates")
    private AnalyticsDates analyticsDates;
}
