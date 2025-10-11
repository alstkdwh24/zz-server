package com.example.zzserver.payment.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ExternalApiConstants {

    ACCESS_TOKEN("https://api.iamport.kr/users/getToken"),
    CANCEL_URL("https://api.iamport.kr/payments/cancel"),
    GET_PAYMENT("https://api.iamport.kr/payments/%s");

    private final String url;

    public String format(Object... args) {
        return String.format(url, args);
    }
}
