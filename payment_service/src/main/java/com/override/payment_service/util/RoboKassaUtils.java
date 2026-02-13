package com.override.payment_service.util;

import com.override.dto.PaymentRequestDTO;
import com.override.payment_service.config.RobokassaConfig;
import com.override.payment_service.exceptions.SignatureNonMatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class RoboKassaUtils {
    public static RobokassaConfig robokassaConfig;

    @Autowired
    public RoboKassaUtils(RobokassaConfig robokassaConfig) {
        this.robokassaConfig = robokassaConfig;
    }

    public static void validateSignature(String httpSignature, String localSignature) {
        if (!httpSignature.equals(localSignature)) {
            throw new SignatureNonMatchException("Сигнатуры не совпадают");
        }
    }
    /**
     * Генерация MD5 хеша
     */
    public static String generateMD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte[] digest = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    public static String constructPaymentUrl(PaymentRequestDTO request, Long invoiceId, String signature, boolean isTest) {
        StringBuilder url = new StringBuilder(robokassaConfig.getBaseUrl());
        url.append("/Merchant/Index.aspx");
        url.append("?MerchantLogin=").append(robokassaConfig.getLoginShopId());
        url.append("&OutSum=").append(request.getAmount());
        url.append("&InvId=").append(invoiceId);
        url.append("&Description=").append(encode(request.getDescription()));
        url.append("&SignatureValue=").append(signature);
        if(isTest){
            url.append("&IsTest=1");
        }
        return url.toString();
    }
    private static String encode(String value) {
        return value != null ? java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8) : "";
    }
}
