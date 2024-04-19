package com.override.invest_service.feign;

import com.override.invest_service.dto.IMOEXDataDTO.IMOEXDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "moex", url = "${moex.index-url-data}")
public interface MOEXFeignClient {

    @GetMapping("/imoex.json?limit=100")
    ResponseEntity<IMOEXDataDTO> getIndexIMOEX();
}
