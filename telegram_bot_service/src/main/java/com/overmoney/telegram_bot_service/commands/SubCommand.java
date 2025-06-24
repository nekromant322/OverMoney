package com.overmoney.telegram_bot_service.commands;

import com.overmoney.telegram_bot_service.constants.Command;
import com.overmoney.telegram_bot_service.service.PaymentService;
import com.overmoney.telegram_bot_service.util.InlineKeyboardMarkupUtil;
import com.override.dto.PaymentRequestDTO;
import com.override.dto.constants.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
public class SubCommand extends OverMoneyCommand {

    private final PaymentService paymentService;
    private final InlineKeyboardMarkupUtil keyboardMarkupUtil;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Value("${payment.amount}")
    private String amount;

    @Value("${payment.currency}")
    private String currency;

    @Value("${payment.description}")
    private String description;

    @Value("${payment.return-url}")
    private String returnUrl;

    public SubCommand(PaymentService paymentService, InlineKeyboardMarkupUtil keyboardMarkupUtil) {
        super(Command.SUB.getAlias(), Command.SUB.getDescription());
        this.paymentService = paymentService;
        this.keyboardMarkupUtil = keyboardMarkupUtil;
    }

    @Override
    public void execute(AbsSender absSender, org.telegram.telegrambots.meta.api.objects.User user,
                        org.telegram.telegrambots.meta.api.objects.Chat chat, String[] arguments) {
        Long chatId = chat.getId();

        // Заглушка для проверки подписки
        boolean isActive = false;
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);

        String messageText = isActive
                ? String.format("Подписка активна до %s", endDate.format(formatter))
                : "Подписка не активна";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText);

        if (!isActive) {
            PaymentRequestDTO request = PaymentRequestDTO.builder()
                    .orderId(UUID.randomUUID().toString())
                    .amount(new BigDecimal(amount))
                    .currency(Currency.valueOf(currency))
                    .returnUrl(returnUrl)
                    .description(description)
                    .build();

            String paymentUrl = paymentService.createPayment(request);

            if (paymentUrl != null) {
                InlineKeyboardMarkup markup = keyboardMarkupUtil.generatePaymentKeyboard(paymentUrl);
                message.setReplyMarkup(markup);
            } else {
                log.error("Failed to create payment for chatId: {}", chatId);
                return;
            }
        }

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending subscription message", e);
        }
    }
}