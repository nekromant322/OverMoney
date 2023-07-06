package com.override.recognizer_service.controller.rest;

import com.override.dto.CategoryDTO;
import com.override.recognizer_service.service.CategoryRecognizerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class RecognizerController {

    @Autowired
    private CategoryRecognizerService categoryRecognizerService;

    @GetMapping("/recognizer")
    public String getRecognizer() {
        log.info("GET request on /recognizer, test");
        return "Recognizer";
    }


    @PostMapping("/recognizer/category/suggested")
    public void recognizeCategory(@RequestParam(name = "message") String message,
                                  @RequestParam(name = "transactionId") UUID transactionId,
                                  @RequestBody List<CategoryDTO> categories) {
        categoryRecognizerService.sendTransactionWithSuggestedCategory(message, categories, transactionId);
    }


}
