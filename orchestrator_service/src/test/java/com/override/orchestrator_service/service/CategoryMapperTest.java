package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.Keyword;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class CategoryMapperTest {

    @InjectMocks
    private CategoryMapper categoryMapper;

    @Test
    public void mapCategoryToJsonResponseTest() {
        final Category category = TestFieldsUtil.generateTestCategory();

        Set<Keyword> keywords = new HashSet<>();
        keywords.add(TestFieldsUtil.generateTestKeyword());
        keywords.add(TestFieldsUtil.generateTestKeyword());

        CategoryDTO categoryDTO = categoryMapper.mapCategoryToJsonResponse(category);

        Assertions.assertEquals(categoryDTO.getId(), category.getId());
        Assertions.assertEquals(StringUtils.capitalize(categoryDTO.getName()),StringUtils.capitalize(category.getName()));
        Assertions.assertEquals(categoryDTO.getType(), category.getType());
        Assertions.assertEquals(categoryDTO.getKeywords().size(), category.getKeywords().size());
    }

    @Test
    public void mapCategoryDTOToCategoryTest() {

        final CategoryDTO category = TestFieldsUtil.generateTestCategoryDTO();

        final Category categoryTest = categoryMapper.mapCategoryDTOToCategory
                (category, TestFieldsUtil.generateTestAccount());

        Assertions.assertEquals(categoryTest.getName(), StringUtils.capitalize(category.getName()));
        Assertions.assertEquals(categoryTest.getType(), category.getType());
    }
}
