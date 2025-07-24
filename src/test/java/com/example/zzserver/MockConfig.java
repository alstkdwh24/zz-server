package com.example.zzserver;

import com.example.zzserver.accommodation.service.AccommodationService;
import com.example.zzserver.accommodation.service.AmenitiesService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public  class MockConfig {
    @Bean
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }

    @Bean
    public AccommodationService accommodationService() {
        return Mockito.mock(AccommodationService.class);
    }

    @Bean
    public AmenitiesService amenitiesService() {
        return Mockito.mock(AmenitiesService.class);
    }
}