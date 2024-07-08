package com.overmoney.telegram_bot_service.commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;

public abstract class OverMoneyCommand extends BotCommand {
    public OverMoneyCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }
}
