package com.override.orchestrator_service.mapper;


import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryMapper {
    public CategoryDTO mapCategoryToJsonResponse(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType().getValue())
                .keywords(getCategoryKeywords(category))
                .build();
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
