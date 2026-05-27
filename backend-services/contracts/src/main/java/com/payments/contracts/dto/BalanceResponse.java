package com.payments.contracts.dto;

public record BalanceResponse(
        String vpa,
        long balancePaise,
        String balanceRupees,
        String currency
) {
    public BalanceResponse(String vpa, long balancePaise) {
        this(vpa, balancePaise, formatRupees(balancePaise), "INR");
    }

    private static String formatRupees(long balancePaise) {
        boolean negative = balancePaise < 0;
        long abs = Math.abs(balancePaise);
        long rupees = abs / 100;
        long paise = abs % 100;
        String sign = negative ? "-" : "";
        return sign + rupees + "." + (paise < 10 ? "0" : "") + paise;
    }
}
