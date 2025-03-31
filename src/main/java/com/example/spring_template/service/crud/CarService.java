package com.example.spring_template.service.crud;

import com.example.spring_template.domain.dto.request.CarRequestDTO;
import com.example.spring_template.domain.dto.response.CarResponseDTO;
import com.example.spring_template.domain.entity.Car;
import com.example.spring_template.repository.CarRepository;
import com.example.spring_template.service.crud.base.BaseService;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class CarService extends BaseService<Car, CarRequestDTO, CarResponseDTO> {
    public CarService(CarRepository repository, ModelMapper modelMapper, MessageSource messageSource) {
        super(repository, modelMapper, Car.class, CarResponseDTO.class, messageSource);
    }
}