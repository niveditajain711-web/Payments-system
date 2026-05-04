package com.payments.psp;

import com.payments.psp.config.PspProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PspProperties.class)
public class PspApplication {

    public static void main(String[] args) {
        SpringApplication.run(PspApplication.class, args);
    }
}
