package com.override.orchestrator_service.service;

import com.override.dto.AccountDataDTO;
import com.override.orchestrator_service.mapper.UserMapper;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
    public void saveUserByAccountDataDTODontSaveIfUserExists() {
        final User user = new User();
        user.setId(1L);
        user.setUsername("Anon");
        AccountDataDTO accountDataDTO = new AccountDataDTO(1L, 1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.saveUser(accountDataDTO);

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void saveUserByAccountDataDTOSaveNewUser() {
        final User user = new User();
        user.setId(1L);
        user.setUsername("Anon");
        AccountDataDTO accountDataDTO = new AccountDataDTO(1L, 1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        userService.saveUser(accountDataDTO);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void saveUserByTelegramAuthRequestSavesNewUser() {
        TelegramAuthRequest telegramAuthRequest = new TelegramAuthRequest();
        telegramAuthRequest.setId(1L);
        telegramAuthRequest.setUsername("Anon");

        final User user = new User();
        user.setId(1L);
        user.setUsername("Anon");

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(userMapper.mapTelegramAuthToUser(telegramAuthRequest)).thenReturn(user);

        userService.saveUser(telegramAuthRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void saveUserByTelegramAuthRequestDontSaveIfUserExistsAndNotChanged() {
        TelegramAuthRequest telegramAuthRequest = new TelegramAuthRequest();
        telegramAuthRequest.setId(1L);
        telegramAuthRequest.setUsername("Anon");

        final User user = new User();
        user.setId(1L);
        user.setUsername("Anon");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.saveUser(telegramAuthRequest);

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void saveUserByTelegramAuthRequestUpdatesUserIfUserExistsAndChanged() {
        final TelegramAuthRequest telegramAuthRequest = new TelegramAuthRequest();
        telegramAuthRequest.setId(1L);
        telegramAuthRequest.setUsername("Anon");

        final User user = new User();
        user.setId(1L);
        user.setUsername("Anonymous");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.saveUser(telegramAuthRequest);

        verify(userRepository, times(1)).updateUserDetailsByUserId(any(), any(), any(), any(), any(), any());
    }

    @Test
    public void getUserByIdReturnsOptionalUserIfFound() throws InstanceNotFoundException {
        final User user = new User();
        user.setId(1L);
        user.setUsername("Anonymous");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User foundUser = userService.getUserById(user.getId());

        Assertions.assertEquals(user, foundUser);
    }




}
