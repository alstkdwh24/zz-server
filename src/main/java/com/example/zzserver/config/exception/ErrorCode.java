package com.example.zzserver.config.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ACCOMMODATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 숙소를 찾을 수 없습니다."),
    ACCOMMODATION_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 숙소 이미지를 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 방을 찾을 수 없습니다."),
    ROOM_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 이미지를 찾을 수 없습니다."),
    AMENITIES_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 편의시설을 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND,"예약을 찾을수 없습니다."),
    RESERVATION_OVERLAP(HttpStatus.BAD_REQUEST, "이미 예약된 기간입니다."),
    RESERVATION_CONFIRM(HttpStatus.BAD_REQUEST,"PENDING 상태에서만 CONFIRMED 가능"),
    RESERVATION_CANCEL_OVERLAP(HttpStatus.BAD_REQUEST, "이미 취소된 예약입니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 카트를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
