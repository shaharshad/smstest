package com.test.cardinalhealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class CardinalhealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardinalhealthApplication.class, args);
    }

}
