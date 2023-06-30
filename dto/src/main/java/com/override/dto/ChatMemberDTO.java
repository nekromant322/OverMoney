package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMemberDTO {
    private Long chatId;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
}
