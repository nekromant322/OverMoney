package com.override.orchestrator_service.controller.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author etozhealexis
 * Контроллер, предоставляющий имя бота для создания кнопки OAuth на страниуе логина
 */

@RestController
@RequestMapping("/login")
public class LoginPageController {
    /**
     * Поле, содержащее юзернейм бота из переменной окружения
     */
    @Value("${telegram.bot.name}")
    private String botName;

    /**
     * Метод GET-запроса (замаппен на /bot-login), предоставляющий юзернейм бота при вызове
     * @return возвращает botName из переменной окружения
     */
    @GetMapping("/bot-login")
    public String getLogin(){
        return botName;
    }
}
