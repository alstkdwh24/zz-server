package com.example.zzserver.member.dto.response;

import java.time.LocalDateTime;

public class CustomErrorResponseDto {

    private int statusCode;
    private String message;
    private LocalDateTime timeStamp;

    public CustomErrorResponseDto() {}


    public CustomErrorResponseDto(int statusCode, String message, LocalDateTime timeStamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }


}

