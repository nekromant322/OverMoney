package com.override.recognizer_service.controller.rest;

import com.override.dto.CategoryDTO;
import com.override.recognizer_service.feign.OrchestratorFeign;
import com.override.recognizer_service.service.CategoryRecognizerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
public class RecognizerController {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    @Autowired
    private CategoryRecognizerService categoryRecognizerService;

    @GetMapping("/orchestra")
    public String getOrchestraWithFeign() {
        return orchestratorFeign.getOrchestra();
    }

    @GetMapping("/recognizer")
    public String getRecognizer() {
        log.info("GET request on /recognizer, test");
        return "Recognizer";
    }

    @PostMapping("/recognizer/category")
    public CategoryDTO recognizeCategory(@RequestParam(name = "message") String message,
                                           @RequestBody List<CategoryDTO> categories) {
        return categoryRecognizerService.recognizeCategory(message, categories);
    }
}
