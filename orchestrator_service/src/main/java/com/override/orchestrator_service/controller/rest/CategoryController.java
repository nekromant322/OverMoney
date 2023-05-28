package com.override.orchestrator_service.controller.rest;


import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static com.override.orchestrator_service.util.TelegramUtils.getTelegramId;

@RestController
@RequestMapping("/categories")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public List<CategoryDTO> getCategoriesList(Principal principal) throws InstanceNotFoundException {
        return categoryService.findCategoriesListByUserId(getTelegramId(principal));
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategoryByCategoryId(@PathVariable("id") UUID id) {
        return categoryService.findCategoryById(id);
    }

    @PostMapping("/add-default-categories")
    public void addDefaultCategories(Principal principal) throws InstanceNotFoundException {
        categoryService.setDefaultCategoryForAccount(getTelegramId(principal));
    }

    @PostMapping("/")
    public ResponseEntity<HttpStatus> createCategoryForAcc(Principal principal, @RequestBody CategoryDTO category) throws InstanceNotFoundException {
        categoryService.saveCategoryForAcc(getTelegramId(principal), category);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }
}
