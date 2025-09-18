package com.overmoney.telegram_bot_service.config;

import com.override.dto.OnboardingStepDTO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "onboarding")
@Data
public class OnboardingProperties {
    private List<OnboardingStepDTO> steps;
}
