package com.override.payment_service.service;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.config.RobokassaValues;
import com.override.payment_service.kafka.service.ProducerService;
import com.override.payment_service.mapper.PaymentMapper;
import com.override.payment_service.model.Payment;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.repository.PaymentRepository;
import com.override.payment_service.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
class PaymentServiceApplicationTest {

    @Autowired
    private RoboKassaService roboKassaService;

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private PaymentMapper paymentMapper;

    @MockBean
    private RobokassaValues robokassaValues;

    @MockBean
    private ProducerService producerService;

    private static final Long TEST_CHAT_ID = 123456789L;
    private static final Long TEST_INVOICE_ID = 1001L;
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(300);
    private static final String TEST_MERCHANT_LOGIN = "test_merchant";
    private static final String TEST_PASSWORD_ONE = "test_pass1";
    private static final String TEST_PASSWORD_TWO = "test_pass2";
    private static final String TEST_API_URL = "https://test.robokassa.ru";

    @BeforeEach
    void setUp() {
        reset(subscriptionService, subscriptionRepository, paymentRepository,
                paymentMapper, robokassaValues, producerService);

        when(robokassaValues.getMerchantLogin()).thenReturn(TEST_MERCHANT_LOGIN);
        when(robokassaValues.getPasswordOne()).thenReturn(TEST_PASSWORD_ONE);
        when(robokassaValues.getPasswordTwo()).thenReturn(TEST_PASSWORD_TWO);
        when(robokassaValues.getApiUrl()).thenReturn(TEST_API_URL);
    }

    @Test
    void createPayment_WhenNewUserCreatesSubscription_ShouldGenerateValidPaymentUrl() {
        Payment payment = new Payment()
                .setInvoiceId(TEST_INVOICE_ID)
                .setAmount(TEST_AMOUNT)
                .setPaymentStatus(PaymentStatus.PENDING);

        when(subscriptionService.findByChatId(TEST_CHAT_ID)).thenReturn(Collections.emptyList());
        when(paymentMapper.toModel(any(PaymentRequestDTO.class))).thenReturn(payment);
        doNothing().when(subscriptionService).save(any(Subscription.class));

        ResponseEntity<String> response = roboKassaService.createPayment(TEST_CHAT_ID);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        String responseBody = response.getBody();
        assertThat(responseBody)
                .contains(TEST_API_URL + "/Merchant/Index.aspx")
                .contains("MerchantLogin=" + TEST_MERCHANT_LOGIN)
                .contains("OutSum=" + TEST_AMOUNT)
                .contains("InvId=" + TEST_INVOICE_ID)
                .contains("SignatureValue=");

        String encodedDescription = URLEncoder.encode("оплата подписки", StandardCharsets.UTF_8);
        assertThat(responseBody).contains("Description=" + encodedDescription);

        verify(subscriptionService).findByChatId(TEST_CHAT_ID);
        verify(paymentMapper).toModel(any(PaymentRequestDTO.class));
        verify(subscriptionService, times(2)).save(any(Subscription.class));
    }

    @Test
    void updatePaymentStatus_WhenValidPaymentReceived_ShouldActivateSubscriptionAndReturnOK() {
        // Arrange
        // Важно: В методе updatePaymentStatus используется BigDecimal.valueOf(Double.parseDouble("300.00"))
        // Это значит, что из строки "300.00" создается BigDecimal с scale 6
        // Поэтому нам нужно использовать правильный формат

        String amountStr = "300.000000";

        // Генерируем сигнатуру как это делает сервис
        String signatureData = String.format("%s:%s:%s",
                amountStr,
                TEST_INVOICE_ID,
                TEST_PASSWORD_TWO);

        String validSignature = generateMD5(signatureData).toUpperCase();

        System.out.println("Generated signature data: " + signatureData);
        System.out.println("Generated signature: " + validSignature);

        Map<String, String> params = new HashMap<>();
        params.put("InvId", TEST_INVOICE_ID.toString());
        params.put("OutSum", "300.00");
        params.put("SignatureValue", validSignature);

        Payment payment = new Payment()
                .setInvoiceId(TEST_INVOICE_ID)
                .setPaymentStatus(PaymentStatus.PENDING);

        Subscription subscription = new Subscription()
                .setChatId(TEST_CHAT_ID)
                .setActive(false)
                .setPayment(payment);

        when(paymentRepository.findByInvoiceId(TEST_INVOICE_ID)).thenReturn(payment);
        when(subscriptionRepository.findByPayment_InvoiceId(TEST_INVOICE_ID)).thenReturn(List.of(subscription));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(producerService).sendSubscriptionNotification(any(PaymentResponseDTO.class));

        ResponseEntity<String> response = roboKassaService.updatePaymentStatus(params);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("OK" + TEST_INVOICE_ID);

        verify(paymentRepository).save(argThat(p ->
                p.getPaymentStatus() == PaymentStatus.SUCCESS
        ));

        verify(subscriptionRepository).save(argThat(Subscription::isActive));

        verify(producerService).sendSubscriptionNotification(any(PaymentResponseDTO.class));
    }

    @Test
    void updatePaymentStatus_WhenInvalidSignature_ShouldReturnBadRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("InvId", TEST_INVOICE_ID.toString());
        params.put("OutSum", "300.00");
        params.put("SignatureValue", "INVALID_SIGNATURE");

        ResponseEntity<String> response = roboKassaService.updatePaymentStatus(params);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).contains("Сигнатуры не совпадают");

        verify(paymentRepository, never()).save(any());
        verify(subscriptionRepository, never()).save(any());
        verify(producerService, never()).sendSubscriptionNotification(any());
    }
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate MD5", e);
        }
    }
}
