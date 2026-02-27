package com.override.orchestrator_service.mapper;

import com.override.dto.ChatMemberDTO;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User mapChatMemberDTOToUser(ChatMemberDTO chatMemberDTO) {
        return User.builder()
                .id(chatMemberDTO.getUserId())
                .username(chatMemberDTO.getUsername())
                .firstName(chatMemberDTO.getFirstName())
                .lastName(chatMemberDTO.getLastName())
                .build();
    }

    public User mapTelegramAuthToUser(TelegramAuthRequest telegramAuthRequest) {
        User user = new User();
        user.setId(telegramAuthRequest.getId());
        user.setUsername(telegramAuthRequest.getUsername());
        user.setFirstName(telegramAuthRequest.getFirst_name());
        user.setLastName(telegramAuthRequest.getLast_name());
        user.setAuthDate(telegramAuthRequest.getAuth_date());
        return user;
    }
}
