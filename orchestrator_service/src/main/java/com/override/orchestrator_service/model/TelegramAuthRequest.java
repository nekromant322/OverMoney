package com.override.orchestrator_service.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TelegramAuthRequest {

    private String id;

    private String first_name;

    private String last_name;

    private String username;

    private String photo_url;

    private String auth_date;

    private String hash;

    @Override
    public String toString() {
        return "auth_date=" + auth_date + "\n"
                + "first_name=" + first_name + "\n"
                + "id=" + id + "\n"
                + "last_name=" + last_name + "\n"
                + "photo_url=" + photo_url + "\n"
                + "username=" + username;

    }
}
