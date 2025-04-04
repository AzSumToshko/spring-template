package com.example.spring_template.service.crud.base;

import com.example.spring_template.domain.entity.base.BaseEntity;
import com.example.spring_template.repository.base.BaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class BaseService<T extends BaseEntity, ReqDTO, ResDTO> {

    protected final BaseRepository<T> repository;
    protected final ModelMapper modelMapper;
    private final Class<T> entityClass;
    private final Class<ResDTO> resDtoClass;
    private final MessageSource messageSource;

    protected BaseService(BaseRepository<T> repository, ModelMapper modelMapper, Class<T> entityClass, Class<ResDTO> resDtoClass, MessageSource messageSource) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.entityClass = entityClass;
        this.resDtoClass = resDtoClass;
        this.messageSource = messageSource;
    }

    public ResDTO save(ReqDTO dto) {
        T entity = modelMapper.map(dto, entityClass);
        T saved = repository.save(entity);
        return modelMapper.map(saved, resDtoClass);
    }

    public ResDTO findById(UUID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage(
                            "error.entity.notfound", new Object[]{id}, LocaleContextHolder.getLocale()
                    );
                    return new EntityNotFoundException(message);
                });
        return modelMapper.map(entity, resDtoClass);
    }

    public Page<ResDTO> findAll(Pageable pageable) {
        Page<T> page = repository.findAll(pageable);
        List<ResDTO> dtoList = page.stream()
                .map(entity -> modelMapper.map(entity, resDtoClass))
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    public ResDTO update(UUID id, ReqDTO dto) {
        if (!repository.existsById(id)) {
            String message = messageSource.getMessage(
                    "error.entity.notfound", new Object[]{id}, LocaleContextHolder.getLocale()
            );
            throw new EntityNotFoundException(message);
        }
        T entity = modelMapper.map(dto, entityClass);
        entity.setId(id);
        T updated = repository.save(entity);
        return modelMapper.map(updated, resDtoClass);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            String message = messageSource.getMessage(
                    "error.entity.notfound", new Object[]{id}, LocaleContextHolder.getLocale()
            );
            throw new EntityNotFoundException(message);
        }

        repository.deleteById(id);
    }
}
