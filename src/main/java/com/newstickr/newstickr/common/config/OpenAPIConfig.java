package com.newstickr.newstickr.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NewsTickr API 문서")
                        .version("1.0")
                        .description("증권 뉴스 감정 평가 및 소통 서비스"));
    }
}