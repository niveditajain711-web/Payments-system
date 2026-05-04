package com.payments.psp.api;

import com.payments.contracts.dto.VpaRegisterResponse;
import com.payments.psp.client.NpciForwardClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/internal/v1/vpas")
public class PspInternalVpaController {

    private final NpciForwardClient npci;

    public PspInternalVpaController(NpciForwardClient npci) {
        this.npci = npci;
    }

    @PutMapping("/{vpa}")
    public ResponseEntity<VpaRegisterResponse> register(@PathVariable String vpa, @RequestBody Map<String, String> body) {
        String path = UriComponentsBuilder.fromPath("/internal/v1/vpas").pathSegment(vpa).build().toUriString();
        return npci.putJson(path, body, VpaRegisterResponse.class);
    }
}
