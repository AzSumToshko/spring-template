package com.example.spring_template.domain.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EngineRequestDTO {
    private String model;
    private int horsepower;
}
