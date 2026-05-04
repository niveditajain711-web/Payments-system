package com.payments.npci.api;

import com.payments.contracts.dto.internal.VpaResolveResponse;
import com.payments.npci.domain.VpaDirectoryEntity;
import com.payments.npci.repo.VpaDirectoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/vpas")
public class InternalVpaController {

    private final VpaDirectoryRepository vpaDirectory;

    public InternalVpaController(VpaDirectoryRepository vpaDirectory) {
        this.vpaDirectory = vpaDirectory;
    }

    @GetMapping("/{vpa}")
    public ResponseEntity<VpaResolveResponse> resolve(@PathVariable String vpa) {
        return vpaDirectory.findById(vpa)
                .map(row -> ResponseEntity.ok(new VpaResolveResponse(row.getVpa(), row.getBankCode(), row.getAccountId().toString())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{vpa}")
    public VpaResolveResponse register(@PathVariable String vpa, @RequestBody Map<String, String> body) {
        String bankCode = body.get("bankCode");
        UUID accountId = UUID.fromString(body.get("accountId"));
        vpaDirectory.save(new VpaDirectoryEntity(vpa, bankCode, accountId));
        return new VpaResolveResponse(vpa, bankCode, accountId.toString());
    }
}
