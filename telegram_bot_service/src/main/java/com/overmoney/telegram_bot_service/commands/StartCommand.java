package com.overmoney.telegram_bot_service.commands;

import com.overmoney.telegram_bot_service.constants.Command;
import com.overmoney.telegram_bot_service.service.OrchestratorRequestService;
import com.override.dto.AccountDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static com.overmoney.telegram_bot_service.constants.Command.START;
import static com.overmoney.telegram_bot_service.constants.MessageConstants.NETWORK_ERROR;

@Component
public class StartCommand extends OverMoneyCommand {
    @Autowired
    private OrchestratorRequestService orchestratorRequestService;
    @Value("${orchestrator.host}")
    private String orchestratorHost;

    @Autowired
    public StartCommand() {
        super(START.getAlias(), START.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Long chatId = chat.getId();
        Long userId = user.getId();
        boolean isRegistrationSuccess =
                orchestratorRequestService.registerSingleAccount(new AccountDataDTO(chatId, userId));
        if (isRegistrationSuccess) {
            sendMessage(absSender, chatId, Command.START.getDescription() + orchestratorHost);
        } else {
            sendMessage(absSender, chatId, NETWORK_ERROR);
        }
    }
}
