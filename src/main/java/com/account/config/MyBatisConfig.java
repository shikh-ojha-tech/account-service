package com.account.config;

import com.account.persistence.type.UuidTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@MapperScan("com.account.persistence")
public class MyBatisConfig {

    @Bean
    ConfigurationCustomizer uuidTypeHandlerCustomizer() {
        return configuration ->
                configuration.getTypeHandlerRegistry().register(UUID.class, UuidTypeHandler.class);
    }
}
