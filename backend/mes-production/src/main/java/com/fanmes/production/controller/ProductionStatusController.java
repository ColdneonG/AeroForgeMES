package com.fanmes.production.controller;

import com.fanmes.common.api.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProductionStatusController {
    @Value("${spring.application.name}")
    private String serviceName;

    @GetMapping("/internal/status")
    public ApiResponse<Map<String, String>> status() {
        return ApiResponse.ok(Map.of("service", serviceName, "status", "UP"));
    }
}
