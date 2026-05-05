package com.payments.npci.service;

import com.payments.contracts.dto.BalanceResponse;
import com.payments.contracts.http.ApiPaths;
import com.payments.npci.config.NpciProperties;
import com.payments.npci.repo.VpaDirectoryRepository;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BalanceQueryService {

    private final VpaDirectoryRepository vpaDirectory;
    private final NpciProperties props;
    private final JdkClientHttpRequestFactory requestFactory;

    public BalanceQueryService(VpaDirectoryRepository vpaDirectory, NpciProperties props, JdkClientHttpRequestFactory requestFactory) {
        this.vpaDirectory = vpaDirectory;
        this.props = props;
        this.requestFactory = requestFactory;
    }

    public BalanceResponse balance(String vpa) {
        var row = vpaDirectory.findById(vpa).orElseThrow(() -> new IllegalArgumentException("Unknown VPA"));
        String base = "A".equalsIgnoreCase(row.getBankCode()) ? props.bankABaseUrl() : props.bankBBaseUrl();
        RestClient client = RestClient.builder().baseUrl(base).requestFactory(requestFactory).build();
        return client.get()
                .uri(uriBuilder -> uriBuilder.path(ApiPaths.BANK_BASE + ApiPaths.BALANCE).queryParam("vpa", vpa).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(BalanceResponse.class);
    }
}
