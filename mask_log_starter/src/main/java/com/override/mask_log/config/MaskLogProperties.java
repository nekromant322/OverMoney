package com.override.mask_log.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "mask-log-spring-boot-starter")
@Getter
@Setter
public class MaskLogProperties {
            private List<String> secretFields;
}