package com.example.zzserver.config.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum Messages {
    SUCCESS(HttpStatus.OK, "Request was successful."),
    CREATED(HttpStatus.CREATED, "Resource was created successfully."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request. Please check your input."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized access. Please provide valid credentials."),
    LOGIN_SUCCESS(HttpStatus.OK, "Login successful."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied. You do not have permission to access this resource."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred on the server."),
    CONFLICT(HttpStatus.CONFLICT, "Conflict detected. The request could not be completed due to a conflict with the current state of the resource."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "Request was successful but there is no content to return."),
    WELCOME(HttpStatus.OK, "Welcome to the ZZ Server!"),
    TOKEN_CREATED(HttpStatus.CREATED, "Token created successfully.");
    private final HttpStatus httpStatus;
    private final String message;


}
