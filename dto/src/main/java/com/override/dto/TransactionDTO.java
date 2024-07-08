package com.override.dto;

import com.override.dto.constants.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Данные транзакции")
public class TransactionDTO {

    @Schema(description = "ID транзакции")
    private UUID id;

    @Schema(description = "Название категории")
    private String categoryName;

    @Schema(description = "Текст транзакции", example = "пиво 300")
    private String message;

    @Schema(description = "Сумма транзакции")
    private Double amount;

    @Schema(description = "Дата транзакции")
    private LocalDateTime date;

    @Schema(description = "Предполагаемый ID категории")
    private Long suggestedCategoryId;

    @Schema(description = "Телеграмм ID пользователя")
    private Long telegramUserId;

    @Schema(description = "Телеграмм имя пользователя")
    private String telegramUserName;

    @Schema(description = "Тип транзакции", example = "INCOME")
    private Type type;
}
