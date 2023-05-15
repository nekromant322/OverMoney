package com.override.orchestrator_service.mapper;


import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.constants.Type;
import com.override.orchestrator_service.model.Category;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CategoryMapper {
    private final String INCOME = "Доходы";
    private final String EXPENSE = "Расходы";

    public CategoryDTO mapCategoryToJsonResponse(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(category.getName());
        categoryDTO.setType(getCategoryType(category));
        categoryDTO.setKeywords(getCategoryKeywords(category));
        return categoryDTO;
    }

    public List<CategoryDTO> mapCategoriesListToJsonResponse(List<Category> categories) {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        categories.stream().forEach(category -> {
            categoryDTOList.add(mapCategoryToJsonResponse(category));
        });
        return categoryDTOList;
    }

    private String getCategoryType(Category category) {
        if (category.getType() == Type.EXPENSE) {
            return EXPENSE;
        }
        if (category.getType() == Type.INCOME) {
            return INCOME;
        }
        return null;
    }

    private List<String> getCategoryKeywords(Category category) {
        List<String> allKeywords = new ArrayList<>();
        category.getKeywords().stream().forEach(keyword -> {
            allKeywords.add(keyword.getKeyword());
        });
        return allKeywords;
    }
}
