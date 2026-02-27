package com.override.payment_service.service.robokassa;

import com.override.payment_service.config.RobokassaConfig;
import com.override.payment_service.exceptions.SignatureNonMatchException;
import com.override.payment_service.model.Payment;
import com.override.payment_service.model.PaymentCallback;
import com.override.payment_service.service.robokassa.signature.SignatureStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class RoboKassaService {
    private final SignatureStrategy signatureStrategy;
    private final RobokassaConfig robokassaConfig;

    public String buildPaymentUrl(Payment payment) {

        String signature = signatureStrategy.generatePaymentSignature(
                robokassaConfig.getLoginShopId(),
                payment.getAmount(),
                payment.getInvoiceId()
        );

        StringBuilder url = new StringBuilder(robokassaConfig.getBaseUrl());
        url.append("/Merchant/Index.aspx");
        url.append("?MerchantLogin=").append(robokassaConfig.getLoginShopId());
        url.append("&OutSum=").append(payment.getAmount());
        url.append("&InvId=").append(payment.getInvoiceId());
        url.append("&Description=").append(encode(payment.getDescription(), UTF_8));
        url.append("&SignatureValue=").append(signature);
        if (robokassaConfig.isTestMode()) {
            url.append("&IsTest=1");
        }
        return url.toString();
    }

    public void validatePaymentCallbackSignature(PaymentCallback paymentCallback) {
        String localSignature = signatureStrategy.generateCallbackSignature(
                paymentCallback.getInvoiceId(), paymentCallback.getPayedSum());
        if (!localSignature.equals(paymentCallback.getHttpSignature())) {
            throw new SignatureNonMatchException("Сигнатуры не совпадают");
        }
    }
}

