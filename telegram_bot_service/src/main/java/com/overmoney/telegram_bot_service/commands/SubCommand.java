package com.overmoney.telegram_bot_service.commands;

import com.overmoney.telegram_bot_service.constants.Command;
import com.overmoney.telegram_bot_service.exception.InvalidPaymentUrlException;
import com.overmoney.telegram_bot_service.service.PaymentService;
import com.overmoney.telegram_bot_service.util.InlineKeyboardMarkupUtil;
import com.override.dto.PaymentRequestDTO;
import com.override.dto.constants.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SubCommand extends OverMoneyCommand {

    private final PaymentService paymentService;
    private final InlineKeyboardMarkupUtil keyboardMarkupUtil;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final Pattern YOOKASSA_URL_PATTERN =
            Pattern.compile("^https://yoomoney\\.ru/.*|^https://\\.yookassa\\.ru/.*");
    private static final String YOOKASSA_DOMAIN = "yookassa.ru";

    @Value("${subscription.price}")
    private String amount;

    @Value("${subscription.currency}")
    private String currency;

    @Value("${subscription.payment.description}")
    private String description;

    @Value("${subscription.payment.return-url}")
    private String returnUrl;

    public SubCommand(PaymentService paymentService, InlineKeyboardMarkupUtil keyboardMarkupUtil) {
        super(Command.SUB.getAlias(), Command.SUB.getDescription());
        this.paymentService = paymentService;
        this.keyboardMarkupUtil = keyboardMarkupUtil;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long chatId = chat.getId();

        // Заглушка для проверки подписки
        boolean isActive = false;
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);

        String messageText = isActive
                ? String.format("Подписка активна до %s", endDate.format(formatter))
                : "Подписка не активна";

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
                try {
                    validatePaymentUrl(paymentUrl);
                    InlineKeyboardMarkup markup = keyboardMarkupUtil.generatePaymentKeyboard(paymentUrl);
                    SendMessage message = new SendMessage(chatId.toString(), messageText);
                    message.setReplyMarkup(markup);
                    try {
                        absSender.execute(message);
                    } catch (TelegramApiException e) {
                        log.error("Ошибка отправки сообщения о подписке", e);
                        sendMessage(absSender, chatId, "Ошибка при отправке платежа");
                    }
                    return;
                } catch (InvalidPaymentUrlException e) {
                    log.error("Некорректный URL платежа: {}. Ожидается домен {}",
                            paymentUrl, YOOKASSA_DOMAIN, e);
                    messageText = "Ошибка: получен некорректный адрес платежа. Сообщите администратору";
                }
            } else {
                log.error("Не удалось создать платеж. NULL в paymentUrl для chatId: {}", chatId);
                messageText = "Произошла ошибка при создании платежа";
            }
        }
        sendMessage(absSender, chatId, messageText);
    }

    private void validatePaymentUrl(String url) throws InvalidPaymentUrlException {
        if (!YOOKASSA_URL_PATTERN.matcher(url).matches()) {
            throw new InvalidPaymentUrlException(
                    String.format("Недопустимый домен платежа. Ожидается %s, получено: %s",
                            YOOKASSA_DOMAIN, URI.create(url).getHost()));
        }
    }
}