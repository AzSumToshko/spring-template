package com.example.spring_template.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CarResponseDTO {
    private UUID id;
    private String model;
    private String manufacturer;
}