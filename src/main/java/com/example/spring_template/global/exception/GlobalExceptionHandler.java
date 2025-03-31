package com.example.spring_template.global.exception;

import com.example.spring_template.domain.enums.LogLevel;
import com.example.spring_template.service.log.LogService;
import com.example.spring_template.util.AuthContextUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final LogService logService;
    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(LogService logService, MessageSource messageSource) {
        this.logService = logService;
        this.messageSource = messageSource;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            Locale locale
    ) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "error", messageSource.getMessage(fieldError, locale)
                ))
                .collect(toList());

        Map<String, Object> body = new HashMap<>();
        body.put("message", messageSource.getMessage("validation.failed", null, "Validation failed", locale));
        body.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }
}
