package com.rungo.api.global;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "API 서버", version = "v1", description = "API 서버 문서입니다."))
public class SpringDoc {

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("AUTH")
                .pathsToMatch("/api/v1/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("USER")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi marathonApi() {
        return GroupedOpenApi.builder()
                .group("MARATHON")
                .pathsToMatch("/api/v1/marathons/**")
                .build();
    }

    @Bean
    public GroupedOpenApi applicationApi() {
        return GroupedOpenApi.builder()
                .group("APPLICATION")
                .pathsToMatch("/api/v1/applications/**")
                .build();
    }

    @Bean
    public GroupedOpenApi organizerApi() {
        return GroupedOpenApi.builder()
                .group("ORGANIZER")
                .pathsToMatch("/api/v1/organizer/**")
                .build();
    }
}
