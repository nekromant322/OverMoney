package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getUndefinedCategory() {
        List<Category> categories = categoryRepository.findAll();
        for (Category category: categories) {
            if (category.getName().equals("Нераспознанное")) {
                return category;
            }
        }
        return null;
    }
}
