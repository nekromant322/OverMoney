package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.User;
import com.overmoney.telegram_bot_service.repository.UserRepository;
import com.override.dto.UserRegistrationInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Set<Long> getAllUsersIds() {
        return userRepository.findAllIdsBy();
    }

    public List<User> getUsersRegistrationDateBefore(LocalDateTime date) {
        return userRepository.findByRegistrationDateBefore(date);
    }

    public void saveUsers(List<UserRegistrationInfoDto> usersDTO) {
        List<User> users = usersDTO.stream().map(
                        u -> User.builder()
                                .id(u.getId())
                                .registrationDate(u.getRegisterDate())
                                .build()
                )
                .collect(Collectors.toList());
        userRepository.saveAll(users);
    }
}