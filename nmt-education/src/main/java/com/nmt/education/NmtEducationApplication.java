package com.nmt.education;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class NmtEducationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NmtEducationApplication.class, args);
    }

}
