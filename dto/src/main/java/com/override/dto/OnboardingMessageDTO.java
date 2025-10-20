package com.override.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingMessageDTO {
    private Long chatId;
    private UUID messageId;
    private String username;
    private String type;
    private String message;
    private int dayOffset;
    private byte[] image;
}
