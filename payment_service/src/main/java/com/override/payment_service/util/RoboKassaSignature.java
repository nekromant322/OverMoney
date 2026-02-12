package com.override.payment_service.util;

import com.override.payment_service.config.RobokassaConfig;
import com.override.payment_service.exceptions.SignatureNonMatchException;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoboKassaSignature {
    private String signature;
    private RobokassaConfig robokassaConfig;

    public RoboKassaSignature(String signature){
        this.signature = signature;
    }

    public RoboKassaSignature() {
    }

    public void validateSignature( String localSignature) {
        if (!signature.equals(localSignature)) {
            throw new SignatureNonMatchException("Сигнатуры не совпадают");
        }
    }
    /**
     * Генерация подписи для создания платежа. Генерация происходит по принципу "логин:сумма:id:Пароль#1"
     *
     * @return сигнатура
     */
    public String generateSignature(String login, String amount, Long invoiceId) {
        return RoboKassaUtils.generateMD5(
                String.format("%s:%s:%s:%s", login, amount, invoiceId, robokassaConfig.getPasswordOne())
        );
    }
    public String generateSignature(String login, String amount, Long invoiceId, boolean isTest) {
        return RoboKassaUtils.generateMD5(
                String.format("%s:%s:%s:%s", login, amount, invoiceId, robokassaConfig.getTestPasswordOne())
        );
    }
    /**
     * Генерация подписи для проверки статуса
     * Генерация происходит по принципу "сумма:id:Пароль#2"
     *
     * @param invoiceId уникальный id платежа (Payment)
     * @param amount    сумма подписки
     */
    public String generateSignatureForStatus(Long invoiceId, BigDecimal amount) {
        String date = String.format("%s:%s:%s",
                amount,
                invoiceId.toString(),
                robokassaConfig.getPasswordTwo());
        return RoboKassaUtils.generateMD5(date);
    }
    public String generateSignatureForStatus(Long invoiceId, BigDecimal amount, boolean isTest) {
        String date = String.format("%s:%s:%s",
                amount,
                invoiceId.toString(),
                robokassaConfig.getTestPasswordTwo());
        return RoboKassaUtils.generateMD5(date);
    }
}
