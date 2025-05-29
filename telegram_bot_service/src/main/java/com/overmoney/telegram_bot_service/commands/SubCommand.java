package com.overmoney.telegram_bot_service.commands;

import com.overmoney.telegram_bot_service.service.PaymentService;
import com.override.dto.AccountDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static com.overmoney.telegram_bot_service.constants.Command.SUB;

@Component
public class SubCommand extends OverMoneyCommand {

    @Autowired
    private PaymentService paymentService;

    public SubCommand() {
        super(SUB.getAlias(), SUB.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Long chatId = chat.getId();
        Long userId = user.getId();
        String subscriptionPeriod = paymentService.checkSubscription(new AccountDataDTO(chatId, userId));
        sendMessage(absSender, chatId, subscriptionPeriod);
    }
}
