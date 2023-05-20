package com.overmoney.telegram_bot_service.mapper;

import com.overmoney.telegram_bot_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User mapTelegramUsertoUser(org.telegram.telegrambots.meta.api.objects.User user) {
        return User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUserName())
                .build();
    }
}
