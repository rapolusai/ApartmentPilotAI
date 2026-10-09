package com.rapolus.apartmentpilotai.api;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
/**
 * Every client error answers with {"message":...} and its real status. Framework exceptions that were not mapped here used to
 * fall through to the /error dispatch, which the security chain then answered with 401, so the app signed the user out.
 */
@RestControllerAdvice public class ErrorHandler {
    private static ResponseEntity<?> message(HttpStatus status,String text) {
        return ResponseEntity.status(status).body(Map.of("message",text));
    }
    @ExceptionHandler(ApiError.class) public ResponseEntity<?> handle(ApiError e) {
        var response=ResponseEntity.status(e.status());
        if(e.retryAfterSeconds()!=null)response.header("Retry-After",Long.toString(e.retryAfterSeconds()));
        return response.body(Map.of("message",e.getMessage()));
    }
    @ExceptionHandler( {
        IllegalArgumentException.class,java.time.DateTimeException.class,MethodArgumentTypeMismatchException.class,HttpMessageNotReadableException.class
    }
    ) public ResponseEntity<?> invalid(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("message",e instanceof IllegalArgumentException && e.getMessage()!=null ? e.getMessage():"Invalid request values."));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<?> validation(MethodArgumentNotValidException e) {
        String m=e.getBindingResult().getFieldErrors().stream().map(f->f.getField()+": "+f.getDefaultMessage()).findFirst().orElse("Check your input.");
        return ResponseEntity.badRequest().body(Map.of("message",m));
    }
    @ExceptionHandler(MissingServletRequestParameterException.class) public ResponseEntity<?> missingParameter(MissingServletRequestParameterException e) {
        return message(HttpStatus.BAD_REQUEST,e.getParameterName()+" is required.");
    }
    @ExceptionHandler(MissingRequestHeaderException.class) public ResponseEntity<?> missingHeader(MissingRequestHeaderException e) {
        return message(HttpStatus.BAD_REQUEST,e.getHeaderName()+" header is required.");
    }
    @ExceptionHandler(ServletRequestBindingException.class) public ResponseEntity<?> binding(ServletRequestBindingException e) {
        return message(HttpStatus.BAD_REQUEST,"Invalid request values.");
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) public ResponseEntity<?> method(HttpRequestMethodNotSupportedException e) {
        return message(HttpStatus.METHOD_NOT_ALLOWED,"This action does not support "+e.getMethod()+".");
    }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class) public ResponseEntity<?> mediaType(HttpMediaTypeNotSupportedException e) {
        return message(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Send the request as JSON.");
    }
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class) public ResponseEntity<?> notAcceptable(HttpMediaTypeNotAcceptableException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
    @ExceptionHandler(NoResourceFoundException.class) public ResponseEntity<?> noRoute(NoResourceFoundException e) {
        return message(HttpStatus.NOT_FOUND,"Not found.");
    }
    @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<?> conflict(DataIntegrityViolationException e) {
        return ResponseEntity.status(409).body(Map.of("message","This record conflicts with an existing record. Refresh and check for duplicates."));
    }
}
