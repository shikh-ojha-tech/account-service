package com.account.support;

import com.account.security.ApiKeyAuthFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@TestConfiguration
public class ApiKeyMockMvcConfig {

    @Bean
    MockMvcBuilderCustomizer apiKeyHeaderCustomizer() {
        return builder -> builder.defaultRequest(
                MockMvcRequestBuilders.get("/")
                        .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-api-key")
        );
    }
}
