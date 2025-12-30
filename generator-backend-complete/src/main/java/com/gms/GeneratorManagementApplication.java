package com.gms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class GeneratorManagementApplication {
    
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Colombo"));
        SpringApplication.run(GeneratorManagementApplication.class, args);
    }
}
