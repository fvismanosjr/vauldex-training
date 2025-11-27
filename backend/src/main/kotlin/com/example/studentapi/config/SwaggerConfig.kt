package com.example.studentapi.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Student Management API")
                    .version("1.0.0")
                    .description("API for managing students")
                    .termsOfService("https://example.com/terms")
                    .contact(
                        Contact()
                            .name("API Support")
                            .email("support@example.com")
                            .url("https://example.com/contact")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("http://springdoc.org")
                    )
            )
    }
}
