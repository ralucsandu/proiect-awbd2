package org.example.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gateway API")
                        .version("1.0")
                        .description("API documentation for Gateway Service"));
    }

    @Bean
    public GroupedOpenApi authorServiceApi() {
        return GroupedOpenApi.builder()
                .group("author-service")
                .pathsToMatch("/authors/**")
                .build();
    }

    @Bean
    public GroupedOpenApi bookServiceApi() {
        return GroupedOpenApi.builder()
                .group("book-service")
                .pathsToMatch("/books/**")
                .build();
    }
}
