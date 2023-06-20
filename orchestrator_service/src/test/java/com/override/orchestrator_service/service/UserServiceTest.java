package com.override.orchestrator_service.service;

import com.override.orchestrator_service.mapper.UserMapper;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    public void getAllUsers() {
        List<User> users = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(users);

        List<User> newUsers = userService.getAllUsers();

        Assertions.assertEquals(users, newUsers);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void saveOrUpdateUser() {
        User user = new User();

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }
}
