package com.example.zzserver.member.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.repository.RefreshRepository;

@Service("refreshTokenService")
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;
    private final ModelMapper modelMapper;

    public RefreshTokenService(RefreshRepository refreshRepository,ModelMapper modelMapper) {
        this.refreshRepository = refreshRepository;
   this.modelMapper = new ModelMapper();
    }
    public int insertRefreshToken(String refreshToken) {

        RefreshToken newToken = new RefreshToken();
        newToken.setRefresh_token(refreshToken);
        refreshRepository.save(newToken);

        return 1; // Token inserted successfully

    };
}
