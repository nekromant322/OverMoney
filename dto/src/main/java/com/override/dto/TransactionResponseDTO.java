package com.override.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответные данные транзакции")
public class TransactionResponseDTO {

    @Schema(description = "ID транзакции")
    private UUID id;

    @Schema(description = "Тип транзакции")
    private String type;

    @Schema(description = "Категория транзакции")
    private String category;

    @Schema(description = "Сумма транзакции")
    private String amount;

    @Schema(description = "Описание транзакции", example = "пиво")
    private String comment;

    @Schema(description = "Id чата в котором произведена транзакция")
    private Long chatId;
}
