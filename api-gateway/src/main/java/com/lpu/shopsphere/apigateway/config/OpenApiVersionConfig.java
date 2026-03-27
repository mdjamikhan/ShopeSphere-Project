package com.lpu.shopsphere.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiVersionConfig {

  private static final String SECURITY_SCHEME_NAME = "bearerAuth";

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("ShopSphere API Gateway")
            .version("3.5.11")
            .description(
                "## ShopSphere Microservices — Aggregated API\n\n" +
                "This gateway aggregates all downstream service APIs.\n\n" +
                "### How to use authorization:\n" +
                "1. Use the **Auth Service** tab → call **POST /auth/login** with your credentials\n" +
                "2. Copy the `token` from the response\n" +
                "3. Click the **Authorize 🔒** button at the top-right\n" +
                "4. Paste the token and click **Authorize**\n" +
                "5. All subsequent requests will automatically include `Authorization: Bearer <token>`")
            .contact(new Contact().name("ShopSphere Team")))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .components(new Components()
            .addSecuritySchemes(SECURITY_SCHEME_NAME,
                new SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Paste your JWT token here (do NOT include the 'Bearer ' prefix — SpringDoc adds it automatically).")));
  }
}


