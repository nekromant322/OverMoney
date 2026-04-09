package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.PaymentFeign;
import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.SubscriptionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentFeign paymentFeign;
    private final PaymentResponseHandler paymentResponseHandler;

    public String createPayment(PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentFeign.createPayment(request);
        return response.getPaymentUrl();
    }

    public SubscriptionDTO getSubscriptionStatus(Long chatId) {
        return paymentFeign.getSubscriptionStatus(chatId);
    }
}