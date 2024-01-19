package com.project.saw.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customAPI() {
        return new OpenAPI().info(new Info()
                .title("SAW API")
                .version("1.0")
                .description("Documentation of the saw application, which allows organizers to create events and collect registrations for them. " +
                        "Every logged in user can sign up and comment on the event."));
    }
}
