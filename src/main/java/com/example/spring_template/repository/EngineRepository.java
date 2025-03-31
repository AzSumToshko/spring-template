package com.example.spring_template.repository;

import com.example.spring_template.domain.entity.Engine;
import com.example.spring_template.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngineRepository extends BaseRepository<Engine> { }
