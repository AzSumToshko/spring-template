package com.example.spring_template.global.exception;

import com.example.spring_template.domain.enums.LogLevel;
import com.example.spring_template.service.log.LogService;
import com.example.spring_template.util.AuthContextUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final LogService logService;

    @Autowired
    public GlobalExceptionHandler(LogService logService) {
        this.logService = logService;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleResourceNotFoundException(EntityNotFoundException ex) {
        logService
                .log(LogLevel.ERROR)
                .setSource("GlobalExceptionHandler")
                .setMessage("Entity not found: " + ex.getMessage())
                .setDetails(ex.toString())
                .setUser(AuthContextUtil.getCurrentUserIdentifier().orElse(null))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
