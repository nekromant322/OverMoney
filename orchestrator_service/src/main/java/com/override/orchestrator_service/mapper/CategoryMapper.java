package com.override.orchestrator_service.mapper;


import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.model.Category;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    public CategoryDTO mapCategoryToJsonResponse(Category category) {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name(category.getName())
                .type(category.getType().getValue())
                .keywords(getCategoryKeywords(category))
                .build();
        return categoryDTO;
    }

    public List<CategoryDTO> mapCategoriesListToJsonResponse(List<Category> categories) {
        return categories
                .stream()
                .map(this::mapCategoryToJsonResponse)
                .collect(Collectors.toList());
    }

    private List<String> getCategoryKeywords(Category category) {
        List<String> allKeywords = new ArrayList<>();
        category.getKeywords().stream().forEach(keyword -> {
            allKeywords.add(keyword.getKeyword());
        });
        return allKeywords;
    }
}
