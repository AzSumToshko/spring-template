package com.example.spring_template.service.crud;

import com.example.spring_template.domain.dto.request.EngineRequestDTO;
import com.example.spring_template.domain.dto.response.EngineResponseDTO;
import com.example.spring_template.domain.entity.Engine;
import com.example.spring_template.repository.EngineRepository;
import com.example.spring_template.service.crud.base.BaseService;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class EngineService extends BaseService<Engine, EngineRequestDTO, EngineResponseDTO> {
    public EngineService(EngineRepository repository, ModelMapper modelMapper, MessageSource messageSource) {
        super(repository, modelMapper, Engine.class, EngineResponseDTO.class, messageSource);
    }
}
