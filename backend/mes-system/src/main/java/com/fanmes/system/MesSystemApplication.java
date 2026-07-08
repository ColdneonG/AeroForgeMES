package com.fanmes.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fanmes")
public class MesSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesSystemApplication.class, args);
    }
}
