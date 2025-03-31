package com.example.spring_template.domain.entity;

import com.example.spring_template.domain.entity.base.BaseEntity;
import com.example.spring_template.domain.enums.LogLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel level;

    @Column(nullable = false)
    private String message;

    @Column(length = 1000)
    private String details; // optional, full stacktrace or object dump

    @Column
    private String source; // e.g., class or module name

    @Column
    private String userId; // optional, user who triggered the log

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
