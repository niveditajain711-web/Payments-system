package com.payments.tpap.api;

import com.payments.contracts.http.ApiPaths;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(ApiPaths.TPAP_BASE)
public class TpapDeviceController {

    public record DeviceBindResponse(String status) {}

    @PostMapping(ApiPaths.DEVICES_BIND)
    public DeviceBindResponse bind(@RequestBody(required = false) Map<String, Object> body) {
        return new DeviceBindResponse("BOUND");
    }
}
