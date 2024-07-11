package com.overmoney.telegram_bot_service.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static com.overmoney.telegram_bot_service.constants.Command.WEB;

@Component
public class WebCommand extends OverMoneyCommand {
    @Value("${orchestrator.host}")
    private String orchestratorHost;

    @Autowired
    public WebCommand() {
        super(WEB.getAlias(), WEB.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        sendMessage(absSender, chat.getId(), orchestratorHost);
    }
}
