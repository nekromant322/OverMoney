package com.override.orchestrator_service.controller.rest;

import com.override.dto.UserInfoResponseDTO;
import com.override.orchestrator_service.service.UserService;
import com.override.orchestrator_service.util.TelegramUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.management.InstanceNotFoundException;
import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping("/count")
    public int getUsersCount() {
        return userService.getUsersCount();
    }

    @Operation(summary = "Получить username и фото авторизированного пользователя",
            description = "Получает username и фото авторизированного пользователя в формате строки" +
                    " data:image:<тип фотографии>;base64,<base64url> ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе передана успешно"),
            @ApiResponse(responseCode = "400", description = "Возникла ошибки при попытке установки изображения " +
                    "по ссылке")
    })
    @GetMapping("/current")
    public UserInfoResponseDTO getUserInfo(Principal principal) throws InstanceNotFoundException {
        Long userId = telegramUtils.getTelegramId(principal);
        return userService.getUserInfo(userId);
    }
}
