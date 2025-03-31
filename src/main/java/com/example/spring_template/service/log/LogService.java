package com.example.spring_template.service.log;

import com.example.spring_template.domain.entity.Log;
import com.example.spring_template.domain.enums.LogLevel;
import com.example.spring_template.repository.LogRepository;
import com.example.spring_template.util.AuthContextUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    private final LogRepository logRepository;
    private Log logData;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public LogService log(LogLevel level) {
        this.logData = new Log();
        this.logData.setLevel(level);
        this.logData.setTimestamp(LocalDateTime.now());
        this.logData.setIpAddress(AuthContextUtil.getIpAddress());
        this.logData.setUserAgent(AuthContextUtil.getUserAgent());
        return this;
    }

    public LogService setUser(String userId) {
        this.logData.setUserId(userId);
        return this;
    }

    public LogService setSource(String source) {
        this.logData.setSource(source);
        return this;
    }

    public LogService setMessage(String message) {
        this.logData.setMessage(message);
        return this;
    }

    public LogService setDetails(String details) {
        this.logData.setDetails(details);
        return this;
    }

    public void build() {
        if (logData != null) {
            logRepository.save(logData);
        }
    }
}
