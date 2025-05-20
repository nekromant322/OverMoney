package com.override.payment_service.service;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class YooKassaService {

    @Value("${yookassa.shop-id}")
    private String shopId;

    @Value("${yookassa.secret-key}")
    private String secretKey;

    @Value("${yookassa.api-url}")
    private String apiUrl;

    private final ObjectMapper objectMapper;

    public PaymentResponseDTO createPayment(PaymentRequestDTO request) throws Exception {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", Map.of(
                    "value", request.getAmount().toString(),
                    "currency", request.getCurrency()
            ));
            requestBody.put("capture", request.isCapture());
            requestBody.put("description", request.getDescription());
            requestBody.put("metadata", request.getMetadata());

            // Confirmation settings
            Map<String, String> confirmation = new HashMap<>();
            confirmation.put("type", request.getConfirmationType());
            confirmation.put("return_url", "https://your-shop.com/order/" + request.getOrderId());
            requestBody.put("confirmation", confirmation);

            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            httpPost.setEntity(new StringEntity(requestBodyJson));
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Idempotence-Key", java.util.UUID.randomUUID().toString());

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);

            // Parse response
            Map<String, Object> responseMap = objectMapper.readValue(responseString, Map.class);
            PaymentResponseDTO paymentResponse = new PaymentResponseDTO();
            paymentResponse.setPaymentId((String) responseMap.get("id"));
            paymentResponse.setStatus((String) responseMap.get("status"));
            paymentResponse.setOrderId(request.getOrderId());

            // Get confirmation URL or QR code
            if (responseMap.containsKey("confirmation")) {
                Map<String, Object> confirmationResponse = (Map<String, Object>) responseMap.get("confirmation");
                if (request.getConfirmationType().equals("redirect")) {
                    paymentResponse.setConfirmationUrl((String) confirmationResponse.get("confirmation_url"));
                } else if (request.getConfirmationType().equals("qr")) {
                    paymentResponse.setQrCodeUrl((String) confirmationResponse.get("confirmation_url"));
                }
            }

            return paymentResponse;
        }
    }

    private CloseableHttpClient createHttpClient() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(shopId, secretKey)
        );
        return HttpClients.custom()
                .setDefaultCredentialsProvider(provider)
                .build();
    }
}