package com.override.recognizer_service.controller.rest;

import com.override.dto.CategoryDTO;
import com.override.recognizer_service.service.category.CategoryRecognizerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@RestController
@Slf4j
public class RecognizerController {

    @Autowired
    private CategoryRecognizerService categoryRecognizerService;

    @Autowired
    @Qualifier("diffExecutorService")
    private ExecutorService executorService;

    @PostMapping("/recognizer/category/suggested")
    public ResponseEntity<Object> recognizeCategory(@RequestParam(name = "message") String message,
                                                    @RequestParam(name = "transactionId") UUID transactionId,
                                                    @RequestBody List<CategoryDTO> categories) {

        executorService.submit(() -> {
                    categoryRecognizerService.sendTransactionWithSuggestedCategory(message, categories, transactionId);
                }
        );
        return ResponseEntity.ok().build();
    }
}
