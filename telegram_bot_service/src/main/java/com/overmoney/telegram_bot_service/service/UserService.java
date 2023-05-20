package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.User;
import com.overmoney.telegram_bot_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByTelegramUsername(String username) throws InstanceNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new InstanceNotFoundException("No user with this username:" + username));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
