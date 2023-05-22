package com.override.orchestrator_service.service;

import com.override.orchestrator_service.mapper.UserMapper;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.HashSet;
import java.util.List;
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

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void saveUser(TelegramAuthRequest telegramAuthRequest) {
        try {
            getUserById(telegramAuthRequest.getId());
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
        foundUser.setRoles(user.getRoles());
        userRepository.save(foundUser);
    }

    public Set<Long> getAllUserIds() {
        return userRepository.getAllUserIds();
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private Set<Role> getUserRole() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "ROLE_USER"));
        return roles;
    }
}