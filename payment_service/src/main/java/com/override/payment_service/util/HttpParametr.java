package com.override.payment_service.util;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class HttpParametr {
    private Long invoiceId;
    private BigDecimal outSum;

    private String httpSignature;

    public HttpParametr(Map<String, String> mapOfParametrs){
        this.invoiceId = Long.valueOf(mapOfParametrs.get("InvId"));
        this.outSum = BigDecimal.valueOf(Double.parseDouble(mapOfParametrs.get("OutSum"))).setScale(6);
        this.httpSignature = mapOfParametrs.get("SignatureValue");
    }

    public BigDecimal getTestOutSum(){
        return outSum.setScale(2);
    }
}
