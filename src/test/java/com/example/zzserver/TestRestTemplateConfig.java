package com.example.zzserver;

import com.example.zzserver.member.service.RealRefreshTokenSevice;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
@TestConfiguration

public class TestRestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
    @Bean
    public RealRefreshTokenSevice realRefreshTokenSevice() {
        return Mockito.mock(RealRefreshTokenSevice.class);
    }

}
