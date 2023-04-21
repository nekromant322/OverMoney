package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.Role;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void saveUser(TelegramAuthRequest telegramAuthRequest) {
        if (Objects.isNull(getUserByUsername(telegramAuthRequest.getUsername()))) {
            User user = new User();
            user.setUsername(telegramAuthRequest.getUsername());
            user.setFirstName(telegramAuthRequest.getFirst_name());
            user.setLastName(telegramAuthRequest.getLast_name());
            user.setPhotoUrl(telegramAuthRequest.getPhoto_url());
            user.setAuthDate(telegramAuthRequest.getAuth_date());
            user.setRoles(getUserRole());
            user.setPassword(passwordEncoder.encode(telegramAuthRequest.getUsername()));
            userRepository.save(user);
        }
    }

    public User getUserById(Long id) throws InstanceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException("Error: no user with this ID"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateUser(User user, Long id) throws InstanceNotFoundException {
        User foundUser = getUserById(id);
        foundUser.setFirstName(user.getFirstName());
        foundUser.setLastName(user.getLastName());
        foundUser.setUsername(user.getUsername());
        foundUser.setPassword(user.getPassword());
        foundUser.setPhotoUrl(user.getPhotoUrl());
        foundUser.setAuthDate(user.getAuthDate());
        foundUser.setRoles(user.getRoles());
        userRepository.save(foundUser);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private Set<Role> getUserRole() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "ROLE_USER"));
        return roles;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }
}
