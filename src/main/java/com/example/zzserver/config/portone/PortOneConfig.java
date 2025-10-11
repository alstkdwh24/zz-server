package com.example.zzserver.config.portone;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PortOneConfig {

    @Value("${portone.v1.api-key}")
    private String api;     // PortOne API 키
    @Value("${portone.v1.api-secret}")
    private String webhook; // Webhook Secret 키

}
