package com.payments.contracts.dto;

public record BalanceResponse(
        String vpa,
        long balancePaise,
        String currency
) {
    public BalanceResponse(String vpa, long balancePaise) {
        this(vpa, balancePaise, "INR");
    }
}
