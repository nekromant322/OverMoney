package com.override.orchestrator_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="recognizer-service")
public interface RecognizerFeign {

    @GetMapping("/recognizer")
    String getRecognition();

}
