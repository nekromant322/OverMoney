package com.override.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author NikitosikQQ
 * DTO класс, необходимый для реализации возможности слияния одной категории в другую
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Данные для слияния/удаления категорий")
public class MergeCategoryDTO {

    @Schema(description = "Id категории, в которую будет произвоиться слияние." +
            "В данную категорию будут перенесены транзакции и ключевые слова категории, " +
            "которая была выбрана для удаления.")
    private Long categoryToChangeId;

    @Schema(description = "Id категории, которая после слияния будет удалена." +
            "Из данной категории будут перенесены транзакции и ключевые слова в категорию," +
            " которая была выбрана для слияния.")
    private Long categoryToMergeId;
}