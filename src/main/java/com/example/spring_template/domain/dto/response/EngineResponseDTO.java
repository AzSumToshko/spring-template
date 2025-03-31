package com.example.spring_template.domain.dto.response;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EngineResponseDTO {
    private UUID id;
    private String model;
    private int horsepower;
}
