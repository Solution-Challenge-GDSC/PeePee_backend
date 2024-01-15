package com.gdsc.solutionchallenge.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info=@Info(title="Boiler-plate API 명세서",
        description = "Springboot Boiler-plate API 명세서",
        version = "v1")
)
@Configuration
public class SwaggerConfig {
}
