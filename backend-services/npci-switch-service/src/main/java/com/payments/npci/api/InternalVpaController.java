package com.payments.npci.api;

import com.payments.contracts.dto.internal.VpaResolveResponse;
import com.payments.contracts.http.ApiPaths;
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
@RequestMapping(ApiPaths.NPCI_BASE)
public class InternalVpaController {

    private final VpaDirectoryRepository vpaDirectory;

    public InternalVpaController(VpaDirectoryRepository vpaDirectory) {
        this.vpaDirectory = vpaDirectory;
    }

    @GetMapping(ApiPaths.REGISTER_VPA_WITH_ID)
    public ResponseEntity<VpaResolveResponse> resolve(@PathVariable("vpaId") String vpaId) {
        return vpaDirectory.findById(vpaId)
                .map(row -> ResponseEntity.ok(new VpaResolveResponse(row.getVpa(), row.getBankCode(), row.getAccountId().toString())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(ApiPaths.REGISTER_VPA_WITH_ID)
    public VpaResolveResponse register(@PathVariable("vpaId") String vpaId, @RequestBody Map<String, String> body) {
        String bankCode = body.get("bankCode");
        UUID accountId = UUID.fromString(body.get("accountId"));
        vpaDirectory.save(new VpaDirectoryEntity(vpaId, bankCode, accountId));
        return new VpaResolveResponse(vpaId, bankCode, accountId.toString());
    }
}
