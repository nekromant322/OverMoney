package com.override.orchestrator_service.controller.rest;


import com.override.dto.CategoryDTO;
import com.override.dto.MergeCategoryDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.KeywordId;
import com.override.orchestrator_service.service.CategoryService;
import com.override.orchestrator_service.util.TelegramUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Контроллер категорий", description = "Операции относящиеся к категориям")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping("/")
    @Operation(summary = "Получить список категорий", description = "Возвращает список категорий, связанных с ID пользователя")
    public List<CategoryDTO> getCategoriesList(Principal principal) throws InstanceNotFoundException {
        return categoryService.findCategoriesListByUserId(telegramUtils.getTelegramId(principal));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить категорию по ID", description = "Возвращает категорию по указанному ID")
    public CategoryDTO getCategoryByCategoryId(
            @Parameter(description = "ID категории") @PathVariable("id") Long id) {
        return categoryService.findCategoryById(id);
    }

    @GetMapping("/types/{type}")
    @Operation(summary = "Получить категории по типу", description = "Возвращает список категорий по указанному типу")
    public List<CategoryDTO> getCategoryByCategoryType(Principal principal,
                                                       @Parameter(description = "Тип категории") @PathVariable("type") Type type) throws InstanceNotFoundException {
        return categoryService.findCategoriesListByType(telegramUtils.getTelegramId(principal), type);
    }

    @PostMapping("/add-default-categories")
    @Operation(summary = "Добавить стандартные категории", description = "Добавляет стандартные категории в аккаунт")
    public void addDefaultCategories(Principal principal) throws InstanceNotFoundException {
        categoryService.setDefaultCategoryForAccount(telegramUtils.getTelegramId(principal));
    }

    @PostMapping("/")
    @Operation(summary = "Сохранить категорию", description = "Сохраняет новую для аккаунта категорию")
    public ResponseEntity<HttpStatus> saveCategoryForAcc(Principal principal,
                                                         @Parameter(description = "Данные категории") @RequestBody CategoryDTO category) throws InstanceNotFoundException {
        categoryService.saveCategoryForAcc(telegramUtils.getTelegramId(principal), category);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PutMapping("/")
    @Operation(summary = "Обновить категорию", description = "Обновляет категорию для аккаунта")
    public ResponseEntity<HttpStatus> updateCategoryForAcc(Principal principal,
                                                           @Parameter(description = "Данные категории") @RequestBody CategoryDTO category) throws InstanceNotFoundException {
        categoryService.updateCategoryForAcc(telegramUtils.getTelegramId(principal), category);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PostMapping("/merge")
    @Operation(summary = "Объединить категории", description = "Объединяет категории по указанным ID")
    public ResponseEntity<HttpStatus> mergeCategory(
            @Parameter(description = "Данные для объединения категорий") @RequestBody MergeCategoryDTO categoryIDs) {
        categoryService.mergeCategory(categoryIDs);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/keywords")
    @Operation(summary = "Удалить ключевое слово", description = "Удаляет ключевое слово по указанному ID")
    public ResponseEntity<HttpStatus> deleteKeyword(
            @Parameter(description = "ID ключевого слова") @RequestBody KeywordId keywordId) {
        categoryService.deleteKeyword(keywordId);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
