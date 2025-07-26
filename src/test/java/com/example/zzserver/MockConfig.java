package com.example.zzserver;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.example.zzserver.accommodation.service.AccommodationService;
import com.example.zzserver.accommodation.service.AmenitiesService;
import com.example.zzserver.accommodation.service.RoomsAmenitiesService;
import com.example.zzserver.accommodation.service.RoomsService;

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

    @Bean
    public RoomsAmenitiesService roomsAmenitiesService() {
        return Mockito.mock(RoomsAmenitiesService.class);
    }

    @Bean
    public RoomsService roomsService() {
        return Mockito.mock(RoomsService.class);
    }
}