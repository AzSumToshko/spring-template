package com.example.spring_template.controller.base;

import com.example.spring_template.domain.entity.base.BaseEntity;
import com.example.spring_template.service.crud.base.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public abstract class BaseController<T extends BaseEntity, ReqDTO, ResDTO> {

    protected final BaseService<T, ReqDTO, ResDTO> service;

    protected BaseController(BaseService<T, ReqDTO, ResDTO> service) {
        this.service = service;
    }

    @Operation(summary = "Create a new entity")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entity created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ResDTO> create(@Valid @RequestBody ReqDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @Operation(summary = "Get entity by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entity found"),
        @ApiResponse(responseCode = "404", description = "Entity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Get all entities (paginated)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entities fetched successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<ResDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Update entity by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entity updated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Entity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResDTO> update(@PathVariable UUID id, @Valid @RequestBody ReqDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Delete entity by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Entity deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Entity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
