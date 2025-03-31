package com.example.spring_template.controller;

import com.example.spring_template.constant.Constants;
import com.example.spring_template.controller.base.BaseController;
import com.example.spring_template.domain.dto.request.EngineRequestDTO;
import com.example.spring_template.domain.dto.response.EngineResponseDTO;
import com.example.spring_template.domain.entity.Engine;
import com.example.spring_template.service.crud.EngineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.API_ENDPOINT + "/engines")
@Tag(name = "Engine API", description = "Operations related to engines.")
public class EngineController extends BaseController<Engine, EngineRequestDTO, EngineResponseDTO> {
    private final ApplicationEventPublisher eventPublisher;

    public EngineController(EngineService service, ApplicationEventPublisher eventPublisher) {
        super(service);
        this.eventPublisher = eventPublisher;
    }
}
