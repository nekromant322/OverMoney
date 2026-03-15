package com.override.payment_service.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class PaymentCallback {
    private Long invoiceId;
    private BigDecimal payedSum;
    private String httpSignature;

    public PaymentCallback(Map<String, String> data) {
        this.invoiceId = Long.valueOf(data.get("InvId"));
        this.payedSum = new BigDecimal(data.get("OutSum"));
        this.httpSignature = data.get("SignatureValue");
    }
}
