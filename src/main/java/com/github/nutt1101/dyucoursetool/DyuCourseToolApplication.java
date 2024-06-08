package com.github.nutt1101.dyucoursetool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DyuCourseToolApplication {
    public static void main(String[] args) {
        SpringApplication.run(DyuCourseToolApplication.class, args);
    }

}
