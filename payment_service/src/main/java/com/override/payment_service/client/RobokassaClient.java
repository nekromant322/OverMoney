package com.override.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "robokassa-client", url = "${robokassa.base-url}")
public interface RobokassaClient {
}