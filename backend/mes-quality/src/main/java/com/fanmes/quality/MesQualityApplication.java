package com.fanmes.quality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fanmes")
public class MesQualityApplication {
    public static void main(String[] args) {
        SpringApplication.run(MesQualityApplication.class, args);
    }
}
