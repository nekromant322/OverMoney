package com.override.orchestrator_service.service;

import com.override.dto.AccountDataDTO;
import com.override.orchestrator_service.mapper.UserMapper;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public int getUsersCount() {
        return userRepository.getUsersCount();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void saveUser(AccountDataDTO accountDataDTO) {
        try {
            getUserById(accountDataDTO.getUserId());
        } catch (InstanceNotFoundException e) {
            User user = new User();
            user.setId(accountDataDTO.getUserId());
            user.setUsername("Anonymous");
            userRepository.save(user);
        }
    }

    @Transactional
    public void saveUser(TelegramAuthRequest telegramAuthRequest) {
        try {
            User user = getUserById(telegramAuthRequest.getId());
            if (!(Objects.equals(user.getUsername(), telegramAuthRequest.getUsername()) &&
                    Objects.equals(user.getFirstName(), telegramAuthRequest.getFirst_name()) &&
                    Objects.equals(user.getLastName(), telegramAuthRequest.getLast_name()) &&
                    Objects.equals(user.getPhotoUrl(), telegramAuthRequest.getPhoto_url()))) {
                userRepository.updateUserDetailsByUserId(telegramAuthRequest.getId(),
                        telegramAuthRequest.getUsername(),
                        telegramAuthRequest.getFirst_name(),
                        telegramAuthRequest.getLast_name(),
                        telegramAuthRequest.getPhoto_url(),
                        telegramAuthRequest.getAuth_date());
            }
        } catch (InstanceNotFoundException e) {
            userRepository.save(userMapper.mapTelegramAuthToUser(telegramAuthRequest));
        }
    }

    public User getUserById(Long id) throws InstanceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException("User with id " + id + " does not exist"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateUser(User user, Long id) throws InstanceNotFoundException {
        User foundUser = getUserById(id);
        foundUser.setFirstName(user.getFirstName());
        foundUser.setLastName(user.getLastName());
        foundUser.setUsername(user.getUsername());
        foundUser.setPhotoUrl(user.getPhotoUrl());
        foundUser.setAuthDate(user.getAuthDate());
        userRepository.save(foundUser);
    }

    public Set<Long> getAllUserIds() {
        return userRepository.getAllUserIds();
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllUsersByIds(userIds);
    }
}