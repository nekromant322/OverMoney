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
@Schema(description = "Данные для определения категории транзакции")
public class TransactionDefineDTO {

    @Schema(description = "ID транзакции")
    private UUID transactionId;

    @Schema(description = "ID категории транзакции")
    private Long categoryId;
}