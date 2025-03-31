package com.example.spring_template.domain.entity;

import com.example.spring_template.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "engines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Engine extends BaseEntity {

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int horsepower;

}
