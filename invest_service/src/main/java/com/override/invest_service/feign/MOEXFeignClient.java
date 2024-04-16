package com.override.invest_service.feign;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "moex", url = "${moex.index-url-data}")
public interface MOEXFeignClient {

    @GetMapping("/imoex.json?limit=100")
    ResponseEntity<JsonNode> getIndexIMOEX();
}
