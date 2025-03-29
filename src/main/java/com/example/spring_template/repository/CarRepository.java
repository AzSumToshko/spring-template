package com.example.spring_template.repository;


import com.example.spring_template.domain.entity.Car;
import com.example.spring_template.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends BaseRepository<Car> {}