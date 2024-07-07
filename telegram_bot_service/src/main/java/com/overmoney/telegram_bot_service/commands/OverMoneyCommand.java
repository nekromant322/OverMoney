package com.overmoney.telegram_bot_service.commands;

import com.override.dto.constants.StatusMailing;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public abstract class OverMoneyCommand extends BotCommand {
    public OverMoneyCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    public void execute(AbsSender sender, SendMessage message, User user) {
        log.info(this.getDescription() + " , пользователь - " + user.getUserName());
        log.info("output: \n" + message.getText() + "\n");
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public StatusMailing sendMessage(AbsSender sender, Long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        log.info("сообщение готовится к отправке " + chatId);
        try {
            sender.execute(message);
            log.info("сообщение отправлено " + chatId);
            return StatusMailing.SUCCESS;
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            return StatusMailing.ERROR;
        }
    }
}
