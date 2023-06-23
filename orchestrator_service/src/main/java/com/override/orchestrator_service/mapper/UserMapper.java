package com.override.orchestrator_service.mapper;

import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User mapTelegramAuthToUser(TelegramAuthRequest telegramAuthRequest) {
        User user = new User();
        user.setId(telegramAuthRequest.getId());
        user.setUsername(telegramAuthRequest.getUsername());
        user.setFirstName(telegramAuthRequest.getFirst_name());
        user.setLastName(telegramAuthRequest.getLast_name());
        user.setPhotoUrl(telegramAuthRequest.getPhoto_url());
        user.setAuthDate(telegramAuthRequest.getAuth_date());
        return user;
    }

}
