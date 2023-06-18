package com.overmoney.telegram_bot_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "max-mailing-messages")
@Getter
@Setter
public class MaxMessagesInSecondProperties {
    public long maxMessagesOfAnnouncePerSecond;
}
