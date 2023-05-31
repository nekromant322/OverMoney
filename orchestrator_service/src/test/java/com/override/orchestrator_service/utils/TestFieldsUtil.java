package com.override.orchestrator_service.utils;

import com.override.orchestrator_service.model.*;

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
}
