package com.override.payment_service.mapper;

import com.override.dto.PaymentRequestDTO;
import com.override.payment_service.model.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {
    public Payment toModel(PaymentRequestDTO paymentRequestDTO) {
        return Payment.builder()
                .amount(paymentRequestDTO.getAmount())
                .description(paymentRequestDTO.getDescription())
                .email(paymentRequestDTO.getDescription())
                .currency(paymentRequestDTO.getCurrency())
                .build();
    }
}
