package com.override.payment_service.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Data
@Service
public class RobokassaValues {
    @Value("${robokassa.login-shop-id}")
    private String merchantLogin;
    @Value("${robokassa.password-one}")
    private String passwordOne;
    @Value("${robokassa.password-two}")
    private String passwordTwo;
    @Value("${robokassa.test-password-one}")
    private String testPasswordOne;
    @Value("${robokassa.test-password-two}")
    private String testPasswordTwo;
    @Value("${robokassa.base-url}")
    private String apiUrl;
    @Value("${robokassa.subscription-amount}")
    private BigDecimal amount;
}
