package com.override.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Данные ключевого слова")
public class KeywordIdDTO {

    @Schema(description = "ID аккаунта пользователя")
    private Long accountId;

    @Schema(description = "Ключевое слово")
    private String name;

    @Schema(description = "Частота использования")
    private Integer frequency;
}
