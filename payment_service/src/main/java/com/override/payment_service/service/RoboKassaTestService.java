package com.override.payment_service.service;

import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.config.RobokassaConfig;
import com.override.payment_service.exceptions.SignatureNonMatchException;
import com.override.payment_service.kafka.service.ProducerService;
import com.override.payment_service.mapper.PaymentMapper;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.repository.PaymentRepository;
import com.override.payment_service.repository.SubscriptionRepository;
import com.override.payment_service.util.HttpParametr;
import com.override.payment_service.util.RoboKassaSignature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Класс использовать исключительно для разработки, должен реализовать интерфейс RoboKassaInterface
 */
@Service
@Slf4j
@Profile("dev")
public class RoboKassaTestService extends RoboKassaService {


    public RoboKassaTestService(SubscriptionService subscriptionService, SubscriptionRepository subscriptionRepository,
                                PaymentRepository paymentRepository, PaymentMapper paymentMapper,
                                RobokassaConfig robokassaConfig, ProducerService producerService) {
        super(subscriptionService, subscriptionRepository, paymentRepository, paymentMapper, robokassaConfig, producerService);
    }
    /**
     * Метод используется при удачной оплате, Robokassa достучится до контроллера, а из контроля вызовется этот метод.
     * С нашей стороны должна происходить проверка сравнения приходящего signatureValue (параметр метода)
     * и сгенерированной параметрами метода (invoiceId, outSum) signature
     *
     * @return при удачной проверке подписей, "OK"+invoiceId - требование Robokassa
     */
    @Transactional
    @Override
    public ResponseEntity<String> updatePaymentStatus(HttpParametr httpParametr) {
        try {
            RoboKassaSignature httpSignature = new RoboKassaSignature(httpParametr.getHttpSignature());
            RoboKassaSignature signatureForStatus = new RoboKassaSignature();

            signatureForStatus.generateSignatureForStatus(httpParametr.getInvoiceId(), httpParametr.getOutSum(),
                    true);
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
}
