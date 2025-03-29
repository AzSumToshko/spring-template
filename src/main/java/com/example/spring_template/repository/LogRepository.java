package com.example.spring_template.repository;

import com.example.spring_template.domain.entity.Log;
import com.example.spring_template.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends BaseRepository<Log> {
}
