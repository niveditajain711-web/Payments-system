package com.payments.npci.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(NpciProperties.class)
public class NpciConfig {

    @Bean
    public JdkClientHttpRequestFactory npciHttpRequestFactory() {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        JdkClientHttpRequestFactory rf = new JdkClientHttpRequestFactory(httpClient);
        rf.setReadTimeout(Duration.ofSeconds(30));
        return rf;
    }
}
