package com.override.payment_service.service.robokassa.signature;

import com.override.payment_service.config.RobokassaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class DevSignatureStrategy implements SignatureStrategy {

    private final RobokassaConfig robokassaConfig;

    @Override
    public String generatePaymentSignature(String shopId, BigDecimal amount, Long invoiceId) {
        return DigestUtils.md5DigestAsHex(String.format("%s:%s:%s:%s",
                shopId,
                amount,
                invoiceId,
                robokassaConfig.getTestPasswordOne()).getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateCallbackSignature(Long invoiceId, BigDecimal payedSum) {
        return DigestUtils.md5DigestAsHex(String.format("%s:%s:%s",
                payedSum,
                invoiceId,
                robokassaConfig.getTestPasswordTwo()).getBytes());
    }
}