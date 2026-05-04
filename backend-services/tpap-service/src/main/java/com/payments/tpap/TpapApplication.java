package com.payments.tpap;

import com.payments.tpap.config.TpapProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TpapProperties.class)
public class TpapApplication {

    public static void main(String[] args) {
        SpringApplication.run(TpapApplication.class, args);
    }
}
