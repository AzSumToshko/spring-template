package com.example.spring_template.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import static com.example.spring_template.constant.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDTO {
    @NotBlank(message = EMAIL_NAME_REQUIRED)
    @Size(max = 100, message = EMAIL_NAME_SIZE)
    private String name;

    @NotBlank(message = EMAIL_REQUIRED)
    @Email(message = EMAIL_VALID)
    private String email;

    @NotBlank(message = EMAIL_SUBJECT_REQUIRED)
    @Size(max = 200, message = EMAIL_SUBJECT_SIZE)
    private String subject;

    @NotBlank(message = EMAIL_MESSAGE_REQUIRED)
    @Size(max = 500, message = EMAIL_MESSAGE_SIZE)
    private String message;
}