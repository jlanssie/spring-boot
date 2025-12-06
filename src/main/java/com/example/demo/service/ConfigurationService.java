package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {
    @Value("${app.env.message}")
    private String environmentMessage;

    public String getEnvironmentMessage() {
        return environmentMessage;
    }
}