package com.override.orchestrator_service.feign;

import com.override.dto.CategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="recognizer-service")
public interface RecognizerFeign {

    @PostMapping("/recognizer/category/suggested")
    CategoryDTO recognizeCategory(@RequestParam String message, @RequestBody List<CategoryDTO> categories);
}
