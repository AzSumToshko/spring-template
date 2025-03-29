package com.example.spring_template.controller.base;

import com.example.spring_template.domain.entity.base.BaseEntity;
import com.example.spring_template.service.crud.base.BaseService;
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

    @PostMapping
    public ResponseEntity<ResDTO> create(@RequestBody ReqDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ResDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResDTO> update(@PathVariable UUID id, @RequestBody ReqDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
