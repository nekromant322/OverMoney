package com.overmoney.telegram_bot_service.mapper;


import com.override.dto.ChatMemberDTO;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class ChatMemberMapper {
    public ChatMemberDTO mapUserToChatMemberDTO(Long chatId, User user) {
        return ChatMemberDTO.builder()
                .chatId(chatId)
                .userId(user.getId())
                .username(user.getUserName())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .build();
    }
}
