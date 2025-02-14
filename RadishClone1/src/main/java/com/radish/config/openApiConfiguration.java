package com.radish.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class openApiConfiguration {

    @Bean
    public OpenAPI expenseApi(){
        return new OpenAPI()
                .info(new Info().title("Reddit Clone API")
                        .description("API for Reddit Clone Application")
                        .version("v0.0.1")
                        .license(new License().name("Apache License Version 2.0").url("https://code-with-meet.web.app")))
                .externalDocs(new ExternalDocumentation()
                        .description("Expense Tracker Wiki Documentation")
                        .url("https://expensetracker.wiki/docs"));
    }
}
