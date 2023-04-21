package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.TelegramAuthRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class TelegramVerificationService {

    @Value("${telegram.bot.token}")
    private String secretKey;

    public boolean verify(TelegramAuthRequest telegramAuthRequest) {
        String hash = telegramAuthRequest.getHash();
        return hash.equals(encodeHmacSha256(telegramAuthRequest.toString()));
    }

    private String encodeHmacSha256(String requestData) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            hmacSha256.init(new SecretKeySpec(MessageDigest.getInstance("SHA-256").digest(secretKey.getBytes(StandardCharsets.UTF_8)), "HmacSHA256"));
            byte[] encodedDataBytes = hmacSha256.doFinal(requestData.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(encodedDataBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.warn("Telegram data encoding failed: " +  e.getMessage() + " cause:" + e.getCause());
        }
        return "";
    }
}
