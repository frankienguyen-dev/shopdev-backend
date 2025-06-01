package com.frankie.ecommerce_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.security.SecureRandom;

@SpringBootApplication
public class EcommerceProjectApplication {

    public static void main(String[] args) {

        SpringApplication.run(EcommerceProjectApplication.class, args);
    }

}
