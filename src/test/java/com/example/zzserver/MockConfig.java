package com.example.zzserver;

import com.example.zzserver.accommodation.service.AccommodationService;
import com.example.zzserver.accommodation.service.AmenitiesService;
import com.example.zzserver.accommodation.service.RoomsAmenitiesService;
import com.example.zzserver.accommodation.service.RoomsService;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import com.example.zzserver.member.service.NaverService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
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

    @Bean
    public RoomsAmenitiesService roomsAmenitiesService() {
        return Mockito.mock(RoomsAmenitiesService.class);
    }

    @Bean
    public RoomsService roomsService() {
        return Mockito.mock(RoomsService.class);
    }

    @Bean
    public NaverService naverService() {
        return Mockito.mock(NaverService.class);
    }

    @Bean
    @Primary

    public RefreshTokenRedisRepository refreshTokenRedisRepository() {
            return Mockito.mock(RefreshTokenRedisRepository.class);
    }


}