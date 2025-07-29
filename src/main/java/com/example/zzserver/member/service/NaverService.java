package com.example.zzserver.member.service;

import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.repository.NaverRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("naverService")
public class NaverService {


    private NaverRepository naverRepository;

    public NaverService(NaverRepository naverRepository) {
        this.naverRepository = naverRepository;
    }

    public String insertRefreshToken(String refreshToken) {

        RefreshToken refreshTokenEntity = new RefreshToken(UUID.randomUUID(),refreshToken);

        System.out.println("insertRefreshToken: " + refreshTokenEntity.getRefresh_token());

        naverRepository.save(refreshTokenEntity);

        return refreshToken;
    }
}
