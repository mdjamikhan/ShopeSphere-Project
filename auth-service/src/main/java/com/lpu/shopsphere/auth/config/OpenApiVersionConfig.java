package com.lpu.shopsphere.auth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiVersionConfig {

  private static final String SECURITY_SCHEME_NAME = "bearerAuth";

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .addServersItem(new Server()
            .url("/gateway")
            .description("Via API Gateway (port 8080) — JWT validated, user headers injected"))
        .info(new Info()
            .title("Auth Service API")
            .version("3.5.11")
            .description("Authentication & JWT token management. " +
                "Use /auth/register to create an account, then /auth/login to get a JWT token. " +
                "Click 'Authorize' and paste the token to test secured endpoints.")
            .contact(new Contact().name("ShopSphere Team")))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .components(new Components()
            .addSecuritySchemes(SECURITY_SCHEME_NAME,
                new SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Paste your JWT token here (without the 'Bearer ' prefix).")));
  }
}

