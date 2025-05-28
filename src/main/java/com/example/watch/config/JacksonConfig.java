package com.example.watch.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Module hibernate5Module() {
        Hibernate5Module module = new Hibernate5Module();
        // Cho phép tự động force-load lazy–loaded associations nếu cần:
        // module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
        return module;
    }
}
