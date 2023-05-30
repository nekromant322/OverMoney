package com.override.orchestrator_service.utils;

import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TestFieldsUtil {

    public static TelegramAuthRequest generateTestAuthRequest() {
        return TelegramAuthRequest.builder()
                .id(123L)
                .first_name("Vasilii")
                .last_name("Matveev")
                .username("kyomexd")
                .photo_url("")
                .auth_date("")
                .hash("")
                .build();
    }

    public static User generateTestUser() {
        return User.builder()
                .id(123L)
                .firstName("Vasilii")
                .lastName("Matveev")
                .username("kyomexd")
                .photoUrl("")
                .authDate("")
                .build();
    }

    public static OverMoneyAccount generateTestAccount() {
        Set<Category> categorySet = new HashSet<>();
        categorySet.add(generateTestCategory());

        Set<User> userSet = new HashSet<>();
        userSet.add(generateTestUser());

        return OverMoneyAccount.builder()
                .id(1L)
                .chatId(404723191L)
                .categories(categorySet)
                .users(userSet)
                .build();
    }

    public static Category generateTestCategory() {
        Set<Keyword> keywordSet = new HashSet<>();
        keywordSet.add(generateTestKeyword());

        return Category.builder()
                .id(UUID.fromString("6060677e-a3d5-4613-9da2-d067597ff095"))
                .name("продукты")
                .type(Type.EXPENSE)
                .keywords(keywordSet)
                .build();
    }

    public static Keyword generateTestKeyword() {
        return Keyword.builder()
                .id(UUID.fromString("6620d7e6-c60f-4928-94d7-40ac77c24fc6"))
                .keyword("пиво")
                .category(Category.builder()
                        .id(UUID.fromString("6060677e-a3d5-4613-9da2-d067597ff095"))
                        .name("продукты")
                        .type(Type.EXPENSE)
                        .build())
                .build();
    }
}
