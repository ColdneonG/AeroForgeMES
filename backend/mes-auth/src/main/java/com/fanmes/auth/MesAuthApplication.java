package com.fanmes.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fanmes")
public class MesAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesAuthApplication.class, args);
    }
}
