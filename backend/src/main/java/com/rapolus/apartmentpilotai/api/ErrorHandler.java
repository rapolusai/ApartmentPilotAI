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
        String m=e.getBindingResult().getFieldErrors().stream().map(f->label(f.getField())+" "+f.getDefaultMessage()+".").findFirst().orElse("Check your input.");
        return ResponseEntity.badRequest().body(Map.of("message",m));
    }
    // A missing parameter or unsupported method is a client error. Without these handlers Spring forwards
    // to /error, which the security chain answered with 401 and the app treated as an expired session.
    @ExceptionHandler( {
        org.springframework.web.bind.MissingServletRequestParameterException.class,org.springframework.web.bind.MissingPathVariableException.class
    }
    ) public ResponseEntity<?> missing(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("message","A required value is missing from the request."));
    }
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class) public ResponseEntity<?> method(Exception e) {
        return ResponseEntity.status(405).body(Map.of("message","This action is not supported."));
    }
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class) public ResponseEntity<?> notFound(Exception e) {
        return ResponseEntity.status(404).body(Map.of("message","Not found."));
    }
    /** "dueDay" -> "Due day", for user-facing validation messages. */
    public static String label(String field) {
        String spaced=field.replaceAll("([a-z])([A-Z])","$1 $2").toLowerCase(java.util.Locale.ROOT);
        return spaced.isEmpty()?spaced:Character.toUpperCase(spaced.charAt(0))+spaced.substring(1);
    }
    @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<?> conflict(DataIntegrityViolationException e) {
        return ResponseEntity.status(409).body(Map.of("message","This record conflicts with an existing record. Refresh and check for duplicates."));
    }
}
