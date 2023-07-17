package com.override.orchestrator_service.mapper;


import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.dto.TransactionExelDTO;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryMapper {
    public CategoryDTO mapCategoryToJsonResponse(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .keywords(getCategoryKeywords(category))
                .build();
    }

    public List<CategoryDTO> mapCategoriesListToJsonResponse(List<Category> categories) {
        return categories
                .stream()
                .map(this::mapCategoryToJsonResponse)
                .sorted()
                .collect(Collectors.toList());
    }

    private List<KeywordIdDTO> getCategoryKeywords(Category category) {
        List<KeywordIdDTO> allKeywords = new ArrayList<>();
        category.getKeywords().forEach(keyword -> {
            KeywordIdDTO keywordIdDTO = KeywordIdDTO.builder()
                    .accountId(keyword.getKeywordId().getAccountId())
                    .name(keyword.getKeywordId().getName())
                    .build();
            allKeywords.add(keywordIdDTO);
        });
        return allKeywords;
    }

    public Category mapCategoryDTOToCategory(CategoryDTO categoryDTO, OverMoneyAccount account) {
        return Category.builder()
                .name(StringUtils.capitalize(categoryDTO.getName()))
                .type(categoryDTO.getType())
                .account(account)
                .build();
    }

    public Category mapTransactionExelDTOToCategory(TransactionExelDTO transactionExelDTO, OverMoneyAccount account) {
        return Category.builder()
                .name(StringUtils.capitalize(transactionExelDTO.getCategory()))
                .type(transactionExelDTO.getType())
                .account(account)
                .build();
    }
}
