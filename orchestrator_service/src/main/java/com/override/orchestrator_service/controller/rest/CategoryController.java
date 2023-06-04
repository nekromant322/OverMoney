package com.override.orchestrator_service.controller.rest;


import com.override.dto.CategoryDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.service.CategoryService;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping("/")
    public List<CategoryDTO> getCategoriesList(Principal principal) throws InstanceNotFoundException {
        return categoryService.findCategoriesListByUserId(telegramUtils.getTelegramId(principal));
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategoryByCategoryId(@PathVariable("id") Long id) {
        return categoryService.findCategoryById(id);
    }

    @GetMapping("/types/{type}")
    public List<CategoryDTO> getCategoryByCategoryId(Principal principal, @PathVariable("type") Type type) throws InstanceNotFoundException {
        return categoryService.findCategoriesListByType(telegramUtils.getTelegramId(principal), type);
    }

    @PostMapping("/add-default-categories")
    public void addDefaultCategories(Principal principal) throws InstanceNotFoundException {
        categoryService.setDefaultCategoryForAccount(telegramUtils.getTelegramId(principal));
    }

    @PostMapping("/")
    public ResponseEntity<HttpStatus> createCategoryForAcc(Principal principal, @RequestBody CategoryDTO category) throws InstanceNotFoundException {
        categoryService.saveCategoryForAcc(telegramUtils.getTelegramId(principal), category);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PostMapping("/merger/{id}")
    public ResponseEntity<HttpStatus> mergeCategory(@RequestBody Long categoryToChangeId, @PathVariable("id") Long categoryToMergeId) throws InstanceNotFoundException {
        categoryService.mergeCategory(categoryToMergeId, categoryToChangeId);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
