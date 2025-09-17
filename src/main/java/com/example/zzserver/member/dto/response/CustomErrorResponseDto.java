package com.example.zzserver.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomErrorResponseDto {

    private int statusCode;
    private String message;
    private LocalDateTime timeStamp;




}

