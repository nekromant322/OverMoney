package com.override.payment_service.service;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.config.RobokassaValues;
import com.override.payment_service.exceptions.RepeatPaymentException;
import com.override.payment_service.exceptions.SignatureNonMatchException;
import com.override.payment_service.kafka.service.ProducerService;
import com.override.payment_service.mapper.PaymentMapper;
import com.override.payment_service.model.Payment;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.repository.PaymentRepository;
import com.override.payment_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoboKassaService implements RoboKassaInterface {
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RobokassaValues robokassaValues;
    private final ProducerService producerService;

    /**
     * Метод создания ссылки на оплату подписки
     *
     * @param chatId уникальный id из Telegram
     * @return ссылка на оплату подписки
     */
    @Transactional
    @Cacheable(value = "chatId", cacheManager = "cacheManager1Min")
    public ResponseEntity<String> createPayment(Long chatId) {
        try {
            PaymentRequestDTO requestDTO = createBasePaymentRequestDTO();
            Subscription subscription = getOrCreateSubscription(chatId);
            validateSubscription(subscription);

            Payment payment = createPayment(requestDTO);
            updateSubscriptionPayment(subscription, payment);

            return ResponseEntity.ok(buildPaymentUrl(requestDTO, subscription.getPayment().getInvoiceId()));
        } catch (RepeatPaymentException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
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
    public ResponseEntity<String> updatePaymentStatus(Map<String, String> allParams) {
        Long invoiceId = Long.valueOf(allParams.get("InvId"));
        BigDecimal outSum = BigDecimal.valueOf(Double.parseDouble(allParams.get("OutSum"))).setScale(6);

        try {
            validateSignature(allParams.get("SignatureValue"),
                    generateSignatureForStatus(invoiceId, outSum).toUpperCase());
        } catch (SignatureNonMatchException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        paymentRepository.save(paymentRepository.findByInvoiceId(invoiceId).setPaymentStatus(PaymentStatus.SUCCESS));
        Subscription subscription = subscriptionRepository.findByPayment_InvoiceId(invoiceId).get(0);
        activateSubscription(subscription);
        producerService.sendSubscriptionNotification(PaymentResponseDTO.builder()
                .message("OK" + invoiceId)
                .chatId(subscription.getChatId())
                .build());

        return ResponseEntity.ok("OK" + invoiceId);
    }

    private void activateSubscription(Subscription subscription) {
        subscriptionRepository.save(subscription
                .setStartDate(LocalDateTime.now())
                .setEndDate(LocalDateTime.now().plusMonths(1))
                .setActive(true));
    }

    private PaymentRequestDTO createBasePaymentRequestDTO() {
        return PaymentRequestDTO.builder()
                .currency(Currency.RUB)
                .description("оплата подписки")
                .amount(BigDecimal.valueOf(300))
                .build();
    }

    private Subscription getOrCreateSubscription(Long chatId) {
        return subscriptionService.findByChatId(chatId)
                .stream()
                .findFirst()
                .orElseGet(() -> createNewSubscription(chatId));
    }

    private Subscription createNewSubscription(Long chatId) {
        Subscription subscription = new Subscription().setChatId(chatId);
        subscriptionService.save(subscription);
        return subscription;
    }

    private void validateSubscription(Subscription subscription) {
        if (subscription.isActive()) {
            throw new RepeatPaymentException("Подписка уже активирована");
        }
    }

    private void validateSignature(String httpSignature, String localSignature) {
        if (!httpSignature.equals(localSignature)) {
            throw new SignatureNonMatchException("Сигнатуры не совпадают");
        }
    }

    private Payment createPayment(PaymentRequestDTO requestDTO) {
        return paymentMapper.toModel(requestDTO)
                .setPaymentStatus(PaymentStatus.PENDING);
    }

    private void updateSubscriptionPayment(Subscription subscription, Payment payment) {
        Optional.ofNullable(subscription.getPayment())
                .ifPresent(paymentRepository::delete);

        subscription.setPayment(payment);
        subscriptionService.save(subscription);
    }

    private String buildPaymentUrl(PaymentRequestDTO requestDTO, Long invoiceId) {
        String amount = requestDTO.getAmount().toString();
        String localSignature = generateSignature(
                robokassaValues.getMerchantLogin(),
                amount,
                invoiceId
        );
        return constructPaymentUrl(requestDTO, invoiceId, localSignature);
    }

    /**
     * Генерация подписи для создания платежа. Генерация происходит по принципу "логин:сумма:id:Пароль#1"
     *
     * @return сигнатура
     */
    private String generateSignature(String login, String amount, Long invoiceId) {
        return generateMD5(String.format("%s:%s:%s:%s", login, amount, invoiceId, robokassaValues.getPasswordOne()));
    }

    /**
     * Генерация подписи для проверки статуса
     * Генерация происходит по принципу "сумма:id:Пароль#2"
     *
     * @param invoiceId уникальный id платежа (Payment)
     * @param amount    сумма подписки
     */
    private String generateSignatureForStatus(Long invoiceId, BigDecimal amount) {
        String date = String.format("%s:%s:%s",
                amount,
                invoiceId.toString(),
                robokassaValues.getPasswordTwo());
        return generateMD5(date);
    }

    /**
     * Генерация MD5 хеша
     */
    private String generateMD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte[] digest = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * Конструкция URL для оплаты
     */
    private String constructPaymentUrl(PaymentRequestDTO request, Long invoiceId, String signature) {
        StringBuilder url = new StringBuilder(robokassaValues.getApiUrl());
        url.append("/Merchant/Index.aspx");
        url.append("?MerchantLogin=").append(robokassaValues.getMerchantLogin());
        url.append("&OutSum=").append(request.getAmount());
        url.append("&InvId=").append(invoiceId);
        url.append("&Description=").append(encode(request.getDescription()));
        url.append("&SignatureValue=").append(signature);

        return url.toString();
    }

    private String encode(String value) {
        return value != null ? java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8) : "";
    }
}
