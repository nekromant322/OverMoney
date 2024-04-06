package com.override.orchestrator_service.feign;

import com.override.dto.CategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "recognizer", url = "${integration.internal.host.recognizer}")
public interface RecognizerFeign {

    @PostMapping("/recognizer/category/suggested")
    CategoryDTO recognizeCategory(@RequestParam String message,
                                                  @RequestParam UUID transactionId,
                                                  @RequestBody List<CategoryDTO> categories);
}
