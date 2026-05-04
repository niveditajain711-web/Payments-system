package com.payments.psp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class PspConfig {

    @Bean
    public JdkClientHttpRequestFactory pspHttpRequestFactory() {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        JdkClientHttpRequestFactory rf = new JdkClientHttpRequestFactory(httpClient);
        rf.setReadTimeout(Duration.ofSeconds(60));
        return rf;
    }

    @Bean
    public RestClient npciRestClient(PspProperties props, JdkClientHttpRequestFactory pspHttpRequestFactory) {
        return RestClient.builder()
                .baseUrl(props.npciBaseUrl())
                .requestFactory(pspHttpRequestFactory)
                .build();
    }
}
