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
import java.util.Objects;

@Service
@Slf4j
public class TelegramVerificationService {

    @Value("${telegram.bot.token}")
    private String secretKey;
    private final String LAST_NAME_REGEX = "last_name=null\n";
    private final String PHOTO_URL_REGEX = "photo_url=null\n";
    private final String HMAC_SHA256 = "HmacSHA256";
    private final String SHA_256 = "SHA-256";

    public boolean verify(TelegramAuthRequest telegramAuthRequest) throws NoSuchAlgorithmException, InvalidKeyException {
        String hash = telegramAuthRequest.getHash();
        return hash.equals(encodeHmacSha256(getHashableData(telegramAuthRequest)));
    }

    private String encodeHmacSha256(String requestData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance(HMAC_SHA256);
        hmacSha256.init(new SecretKeySpec(MessageDigest.getInstance(SHA_256)
                .digest(secretKey.getBytes(StandardCharsets.UTF_8)), HMAC_SHA256));
        byte[] encodedDataBytes = hmacSha256.doFinal(requestData.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(encodedDataBytes);

    }

    private String getHashableData(TelegramAuthRequest telegramAuthRequest) {

        String hashableData = "auth_date=" + telegramAuthRequest.getAuth_date() + "\n"
                + "first_name=" + telegramAuthRequest.getFirst_name() + "\n"
                + "id=" + telegramAuthRequest.getId() + "\n"
                + "last_name=" + telegramAuthRequest.getLast_name() + "\n"
                + "photo_url=" + telegramAuthRequest.getPhoto_url() + "\n"
                + "username=" + telegramAuthRequest.getUsername();

        if (Objects.isNull(telegramAuthRequest.getLast_name())) {
             hashableData = hashableData.replaceAll(LAST_NAME_REGEX, "");
        }
        if (Objects.isNull(telegramAuthRequest.getPhoto_url())) {
            hashableData = hashableData.replaceAll(PHOTO_URL_REGEX, "");
        }

        return hashableData;
    }
}
