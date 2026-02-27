package com.override.orchestrator_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TelegramAuthRequest {

    private Long id;

    private String first_name;

    private String last_name;

    private String username;

    private String photo_url;

    private String auth_date;

    private String hash;
}
