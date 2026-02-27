package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoiceMessageDTO {
    private byte[] voiceMessageBytes;
    private Long userId;
    private Long chatId;
}
