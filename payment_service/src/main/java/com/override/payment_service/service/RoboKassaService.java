package com.override.payment_service.service;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.config.RobokassaConfig;
import com.override.payment_service.exceptions.RepeatPaymentException;
import com.override.payment_service.exceptions.SignatureNonMatchException;
import com.override.payment_service.kafka.service.ProducerService;
import com.override.payment_service.mapper.PaymentMapper;
import com.override.payment_service.model.Payment;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.repository.PaymentRepository;
import com.override.payment_service.repository.SubscriptionRepository;
import com.override.payment_service.util.HttpParametr;
import com.override.payment_service.util.RoboKassaSignature;
import com.override.payment_service.util.RoboKassaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class RoboKassaService {
    protected final SubscriptionService subscriptionService;
    protected final SubscriptionRepository subscriptionRepository;
    protected final PaymentRepository paymentRepository;
    protected final PaymentMapper paymentMapper;
    protected final RobokassaConfig robokassaConfig;
    protected final ProducerService producerService;

    /**
     * Метод создания ссылки на оплату подписки
     *
     * @param chatId уникальный id из Telegram
     * @return ссылка на оплату подписки
     */
    @Transactional
    public String createPayment(Long chatId) {
        try {
            PaymentRequestDTO requestDTO = createBasePaymentRequestDTO();
            Subscription subscription = subscriptionService.getOrCreateSubscription(chatId);

            Payment payment = paymentMapper.toModel(requestDTO)
                    .setPaymentStatus(PaymentStatus.PENDING);

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Saved payment with id: {}, invoiceId: {}", savedPayment.getInvoiceId(),
                    savedPayment.getInvoiceId());

            Optional.ofNullable(subscription.getPayment())
                    .ifPresent(oldPayment -> {
                        subscription.setPayment(null);
                        paymentRepository.delete(oldPayment);
                    });

            subscription.setPayment(savedPayment);
            subscriptionService.save(subscription);

            Subscription savedSubscription = subscriptionRepository.findById(subscription.getId()).orElseThrow();
            log.info("Subscription saved with payment_invoice_id: {}",
                    savedSubscription.getPayment() != null ? savedSubscription.getPayment().getInvoiceId() : "null");

            return buildPaymentUrl(requestDTO, savedPayment.getInvoiceId());

        } catch (RepeatPaymentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating payment: {}", e.getMessage(), e);
            return "";
        }
    }


    /**
     * Метод используется при удачной оплате, Robokassa достучится до контроллера, а из контроля вызовется этот метод.
     * С нашей стороны должна происходить проверка сравнения приходящего signatureValue (параметр метода)
     * и сгенерированной параметрами метода (invoiceId, outSum) signature
     *
     * @return при удачной проверке подписей, "OK"+invoiceId - требование Robokassa
     */

    @Transactional
    public ResponseEntity<String> updatePaymentStatus(HttpParametr httpParametr) {
        try {
            RoboKassaSignature httpSignature = new RoboKassaSignature(httpParametr.getHttpSignature(), robokassaConfig);
            RoboKassaSignature signatureForStatus = new RoboKassaSignature(robokassaConfig);

            signatureForStatus.generateSignatureForStatus(httpParametr.getInvoiceId(), httpParametr.getTestOutSum());
            httpSignature.validateSignature(signatureForStatus.getSignature());
        } catch (SignatureNonMatchException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        paymentRepository.save(paymentRepository.findByInvoiceId(httpParametr.getInvoiceId())
                .setPaymentStatus(PaymentStatus.SUCCESS));

        Subscription subscription = subscriptionRepository.findByPayment_InvoiceId(httpParametr.getInvoiceId())
                .orElseThrow(
                        () -> new IllegalStateException("Subscription not found for invoice: " + httpParametr.getInvoiceId())
                );
        subscriptionRepository.save(subscription
                .setStartDate(LocalDateTime.now())
                .setEndDate(LocalDateTime.now().plusMonths(1))
                .setActive(true));
        producerService.sendSubscriptionNotification(PaymentResponseDTO.builder()
                .message("OK" + httpParametr.getInvoiceId())
                .chatId(subscription.getChatId())
                .build());

        return ResponseEntity.ok("OK" + httpParametr.getInvoiceId());
    }

    protected PaymentRequestDTO createBasePaymentRequestDTO() {
        return PaymentRequestDTO.builder()
                .currency(Currency.RUB)
                .description("оплата подписки")
                .amount(BigDecimal.valueOf(robokassaConfig.getSubscriptionAmount()))
                .build();
    }

    protected String buildPaymentUrl(PaymentRequestDTO requestDTO, Long invoiceId) {
        String amount = requestDTO.getAmount().toString();
        RoboKassaSignature localSignature = new RoboKassaSignature(robokassaConfig);
        localSignature.setSignature(
                localSignature.generateSignature(
                        robokassaConfig.getLoginShopId(),
                        amount,
                        invoiceId));
        return RoboKassaUtils.constructPaymentUrl(requestDTO, invoiceId, localSignature.getSignature(), false);
    }

}
