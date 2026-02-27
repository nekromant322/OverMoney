package com.override.payment_service.service.robokassa.signature;

import java.math.BigDecimal;

public interface SignatureStrategy {
    String generatePaymentSignature(String shopId, BigDecimal amount, Long invoiceId);

    String generateCallbackSignature(Long invoiceId, BigDecimal payedSum);
}
