package com.rapolus.apartmentpilotai.api;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice public class ErrorHandler {
    @ExceptionHandler(ApiError.class) public ResponseEntity<?> handle(ApiError e) {
        return ResponseEntity.status(e.status()).body(Map.of("message",e.getMessage()));
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
    @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<?> conflict(DataIntegrityViolationException e) {
        return ResponseEntity.status(409).body(Map.of("message","This record conflicts with an existing record. Refresh and check for duplicates."));
    }
}
