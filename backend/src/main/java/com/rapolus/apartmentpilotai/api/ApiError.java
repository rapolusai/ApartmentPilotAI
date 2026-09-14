package com.rapolus.apartmentpilotai.api;
import org.springframework.http.HttpStatus;
public class ApiError extends RuntimeException {
    private final HttpStatus status;
    public ApiError(HttpStatus status,String message) {
        super(message);
        this.status=status;
    }
    public HttpStatus status() {
        return status;
    }
    public static ApiError forbidden() {
        return new ApiError(HttpStatus.FORBIDDEN,"You do not have access to this action.");
    }
    public static ApiError missing() {
        return new ApiError(HttpStatus.NOT_FOUND,"Record not found.");
    }
    public static ApiError conflict(String text) {
        return new ApiError(HttpStatus.CONFLICT,text);
    }
}
