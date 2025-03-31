package com.example.spring_template.domain.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {
    private String message;
    private List<FieldErrorDetail> errors;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldErrorDetail {
        private String field;
        private String error;
    }
}