package com.override.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Данные текста транзакции")
public class TransactionMessageDTO {

    @Schema(description = "Текст транзакции", example = "пиво 300")
    private String message;

    @Schema(description = "ID пользователя")
    private Long userId;

    @Schema(description = "Id чата в котором произведена транзакция")
    private Long chatId;

    @Schema(description = "Дата транзакции")
    private LocalDateTime date;
}
