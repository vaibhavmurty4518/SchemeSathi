package in.schemesathi.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ApiError> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
    return ResponseEntity.badRequest().body(new ApiError(Instant.now(),400,"Bad Request",ex.getMessage(),req.getRequestURI()));
  }
  @ExceptionHandler(java.util.NoSuchElementException.class)
  ResponseEntity<ApiError> notFound(java.util.NoSuchElementException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(Instant.now(),404,"Not Found",ex.getMessage(),req.getRequestURI()));
  }
  @ExceptionHandler(IllegalStateException.class)
  ResponseEntity<ApiError> conflict(IllegalStateException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(Instant.now(),409,"Conflict",ex.getMessage(),req.getRequestURI()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    String message = ex.getBindingResult().getFieldErrors().stream().map(e -> e.getField()+": "+e.getDefaultMessage()).findFirst().orElse("Validation failed");
    return ResponseEntity.badRequest().body(new ApiError(Instant.now(),400,"Validation Error",message,req.getRequestURI()));
  }
}
