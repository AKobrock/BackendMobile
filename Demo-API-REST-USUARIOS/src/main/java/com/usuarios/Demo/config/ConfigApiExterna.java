package com.usuarios.Demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigApiExterna {

    @Value("${api.externa.url}")
    private String urlApiExterna;

    public String getUrlApiExterna() {
        return urlApiExterna;
    }
}

