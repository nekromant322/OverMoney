package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryMapper categoryMapper;

    public List<CategoryDTO> findCategoriesListByUsername(String username) {
        Long id = userService.getUserByUsername(username).getId();
        return categoryMapper.mapCategoriesListToJsonResponse(categoryRepository.findAllByUserId(String.valueOf(id)));
    }
}