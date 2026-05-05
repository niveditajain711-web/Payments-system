package com.payments.tpap.api;

import com.payments.contracts.dto.VpaRegisterRequest;
import com.payments.contracts.dto.VpaRegisterResponse;
import com.payments.contracts.http.ApiPaths;
import com.payments.tpap.client.PspForwardClient;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.TPAP_BASE)
public class TpapVpaController {

    private final PspForwardClient psp;

    public TpapVpaController(PspForwardClient psp) {
        this.psp = psp;
    }

    @PostMapping(ApiPaths.REGISTER_VPA)
    public ResponseEntity<VpaRegisterResponse> register(@Valid @RequestBody VpaRegisterRequest body) {
        return psp.registerVpa(body.vpa(), body.bankCode(), body.accountId());
    }
}
