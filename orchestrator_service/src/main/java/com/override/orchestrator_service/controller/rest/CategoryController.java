package com.override.orchestrator_service.controller.rest;


import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import com.override.orchestrator_service.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public List<CategoryDTO> getCategoriesList(Principal principal) throws InstanceNotFoundException {
        return categoryService.findCategoriesListByUserId(((JwtAuthentication)principal).getTelegramId());
    }

    @PostMapping("/add-default-categories")
    public void addDefaultCategories(Principal principal) throws InstanceNotFoundException {
        categoryService.setDefaultCategoryForAccount(((JwtAuthentication)principal).getTelegramId());
    }
}
