package com.override.dto;

import com.override.dto.constants.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Данные категории")
public class CategoryDTO implements Comparable<CategoryDTO> {

    @Schema(description = "ID категории")
    private Long id;

    @Schema(description = "Название категории")
    @NotBlank(message = "Название категории не может быть пустым")
    @Size(max = 40, message = "Название категории не может быть длиннее 50 символов")
    private String name;

    @Schema(description = "Тип категории", example = "INCOME")
    private Type type;

    @Schema(description = "Список ключевых слов")
    private List<KeywordIdDTO> keywords;

    @Override
    public int compareTo(CategoryDTO o) {
        return name.compareTo(o.getName());
    }
}
