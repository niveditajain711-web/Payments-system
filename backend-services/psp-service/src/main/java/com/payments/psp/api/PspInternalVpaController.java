package com.payments.psp.api;

import com.payments.contracts.dto.VpaRegisterResponse;
import com.payments.contracts.http.ApiPaths;
import com.payments.psp.client.NpciForwardClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(ApiPaths.PSP_BASE)
public class PspInternalVpaController {

    private final NpciForwardClient npci;

    public PspInternalVpaController(NpciForwardClient npci) {
        this.npci = npci;
    }

    @PutMapping(ApiPaths.REGISTER_VPA_WITH_ID)
    public ResponseEntity<VpaRegisterResponse> register(@PathVariable("vpaId") String vpaId, @RequestBody Map<String, String> body) {
        return npci.registerVpa(vpaId, body.get("bankCode"), body.get("accountId"));
    }
}
