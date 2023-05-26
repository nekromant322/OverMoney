package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionMessageDTO {
    private String message;
    private String username;
    private Long chatId;
    private Integer date;
}
