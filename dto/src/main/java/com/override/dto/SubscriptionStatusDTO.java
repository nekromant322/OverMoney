package com.override.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionStatusDTO {
    private boolean isActive;
}
