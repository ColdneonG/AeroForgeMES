package com.fanmes.production;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fanmes")
public class MesProductionApplication {
    public static void main(String[] args) {
        SpringApplication.run(MesProductionApplication.class, args);
    }
}
