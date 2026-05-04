package com.payments.tpap.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/devices")
public class TpapDeviceController {

    public record DeviceBindResponse(String status) {}

    @PostMapping("/bind")
    public DeviceBindResponse bind(@RequestBody(required = false) Map<String, Object> body) {
        return new DeviceBindResponse("BOUND");
    }
}
