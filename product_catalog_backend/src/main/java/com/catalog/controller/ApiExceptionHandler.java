package com.catalog.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException exception) {
    String message =
        exception.getBindingResult().getFieldErrors().stream()
            .map(this::fieldMessage)
            .distinct()
            .collect(Collectors.joining("; "));
    return response(HttpStatus.BAD_REQUEST, message.isBlank() ? "Solicitud inválida" : message);
  }

  @ExceptionHandler(ResponseStatusException.class)
  ResponseEntity<Map<String, Object>> status(ResponseStatusException exception) {
    return response(HttpStatus.valueOf(exception.getStatusCode().value()), exception.getReason());
  }

  private String fieldMessage(FieldError error) {
    return error.getField() + ": " + error.getDefaultMessage();
  }

  private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    return ResponseEntity.status(status).body(body);
  }
}
