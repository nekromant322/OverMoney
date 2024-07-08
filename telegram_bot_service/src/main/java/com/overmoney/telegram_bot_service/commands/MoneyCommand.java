package com.overmoney.telegram_bot_service.commands;

import com.overmoney.telegram_bot_service.constants.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static com.overmoney.telegram_bot_service.constants.Command.MONEY;

@Component
public class MoneyCommand extends OverMoneyCommand{
    @Autowired
    public MoneyCommand() {
        super(MONEY.getAlias(), MONEY.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        sendMessage(absSender, chat.getId(), Command.MONEY.getDescription());
    }
}
