package com.override.orchestrator_service.mapper;

import com.override.orchestrator_service.model.Role;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.model.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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
        user.setRoles(getUserRole());
        return user;
    }

    private Set<Role> getUserRole() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "ROLE_USER"));
        return roles;
    }
}
