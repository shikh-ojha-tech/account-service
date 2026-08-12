package com.account.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String API_KEY_SCHEME = "ApiKeyAuth";

    @Bean
    public OpenAPI accountServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Account Service")
                        .description("Accounts, balances, and IN/OUT transactions. "
                                + "Use header X-API-Key (local default: demo-api-key).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("shikh-ojha-tech")
                                .url("https://github.com/shikh-ojha-tech/account-service")))
                .addTagsItem(new Tag().name("Accounts").description("Create and get accounts"))
                .addTagsItem(new Tag().name("Transactions").description("Create and list transactions"))
                .components(new Components().addSecuritySchemes(API_KEY_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY_SCHEME));
    }
}
