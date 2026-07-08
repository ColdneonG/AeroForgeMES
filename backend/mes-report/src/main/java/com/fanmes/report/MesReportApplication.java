package com.fanmes.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fanmes")
public class MesReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesReportApplication.class, args);
    }
}
