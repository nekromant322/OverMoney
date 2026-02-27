package com.override.payment_service.service;

import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.config.PayingConfig;
import com.override.payment_service.exceptions.RepeatPaymentException;
import com.override.payment_service.kafka.service.ProducerService;
import com.override.payment_service.model.Payment;
import com.override.payment_service.model.PaymentCallback;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.service.robokassa.RoboKassaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayingService {
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final ProducerService producerService;
    private final RoboKassaService roboKassaService;
    private final PayingConfig payingConfig;

    /**
     * Метод создания ссылки на оплату подписки
     *
     * @param chatId уникальный id из Telegram
     * @return ссылка на оплату подписки
     */
    @Transactional
    public String createPayment(Long chatId) {
        Subscription subscription = subscriptionService.getOrCreateSubscription(chatId);
        if (subscription.isActive()) {
            throw new RepeatPaymentException("Подписка уже активна");
        }

        //TODO проверить .email (робакасса по идее должна передавать)
        Payment payment = paymentService.save(Payment.builder()
                .paymentStatus(PaymentStatus.PENDING)
                .description("оплата подписки")
                .amount(payingConfig.getSubscriptionAmount())
                .currency(Currency.RUB)
                .build());
        subscriptionService.attachPayment(subscription, payment);
        return roboKassaService.buildPaymentUrl(payment);
    }


    /**
     * Метод используется при удачной оплате, Robokassa достучится до контроллера, а из контроля вызовется этот метод.
     * С нашей стороны должна происходить проверка сравнения приходящего signatureValue (параметр метода)
     * и сгенерированной параметрами метода (invoiceId, outSum) signature
     *
     * @return при удачной проверке подписей, "OK"+invoiceId - требование Robokassa
     */

    @Transactional
    public String handlePaymentCallback(PaymentCallback paymentCallback) {

        roboKassaService.validatePaymentCallbackSignature(paymentCallback);

        Payment payment = paymentService.successPayment(paymentCallback.getInvoiceId());
        Subscription subscription = subscriptionService.activateSubscription(payment);

        producerService.sendSubscriptionNotification(
                PaymentResponseDTO.builder()
                        .message("OK" + paymentCallback.getInvoiceId())
                        .chatId(subscription.getChatId())
                        .build()
        );

        return "OK" + paymentCallback.getInvoiceId();
    }
}
