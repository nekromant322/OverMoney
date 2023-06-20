package com.override.orchestrator_service.mapper;

import com.override.dto.ChatMemberDTO;
import com.override.orchestrator_service.model.Role;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.model.User;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    private final Long MIN_ACCOUNT_ID = 0L;

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
        user.setPhotoUrl(telegramAuthRequest.getPhoto_url());
        user.setAuthDate(telegramAuthRequest.getAuth_date());
        user.setRoles(getUserRole());
        return user;
    }

    private Set<Role> getUserRole() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "ROLE_USER"));
        return roles;
    }

    public List<User> mapUsernameListToUserList(List<String> usernames) {
        return usernames.stream()
                .map(username -> User.builder()
                        .id(RandomUtils.nextLong(MIN_ACCOUNT_ID, Long.MAX_VALUE))
                        .username(username)
                        .build())
                .collect(Collectors.toList());
    }
}
