package com.rapolus.apartmentpilotai.api;
import org.springframework.http.HttpStatus;
public class ApiError extends RuntimeException {
    private final HttpStatus status;
    private final Long retryAfterSeconds;
    public ApiError(HttpStatus status,String message) {
        this(status,message,null);
    }
    private ApiError(HttpStatus status,String message,Long retryAfterSeconds) {
        super(message);
        this.status=status;
        this.retryAfterSeconds=retryAfterSeconds;
    }
    public HttpStatus status() {
        return status;
    }
    /** Seconds for a Retry-After header, or null when the response should not carry one. */
    public Long retryAfterSeconds() {
        return retryAfterSeconds;
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
    public static ApiError tooMany(String text,long retryAfterSeconds) {
        return new ApiError(HttpStatus.TOO_MANY_REQUESTS,text,Math.max(1,retryAfterSeconds));
    }
}
