package com.payments.contracts.http;

public final class ApiPaths {

    public static final String API_V1 = "/api/v1";

    public static final String TPAP_BASE = API_V1 + "/tpap";
    public static final String PSP_BASE = API_V1 + "/psp";
    public static final String NPCI_BASE = API_V1 + "/npci";
    public static final String BANK_BASE = API_V1 + "/bank";

    public static final String PAY = "/payments/pay";
    public static final String COLLECT_REQUESTS = "/collect-requests";
    public static final String COLLECT_APPROVE = "/collect-requests/{id}/approve";
    public static final String BALANCE = "/balance";
    public static final String REGISTER_VPA = "/register/vpa";
    public static final String REGISTER_VPA_WITH_ID = "/register/vpa/{vpaId}";
    public static final String DEVICES_BIND = "/devices/bind";
    public static final String JOURNALS_APPLY = "/journals/apply";

    private ApiPaths() {
    }
}
