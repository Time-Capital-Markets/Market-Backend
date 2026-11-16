package com.fourmint.platform.spring;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.web.servlet.config.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import java.time.LocalDate;
import java.util.Arrays;
@Configuration(proxyBeanMethods = false)
@Import(ApiErrors.class)
public class PlatformConfiguration {
    @Bean
    Jackson2ObjectMapperBuilderCustomizer strictJson() {
        return builder -> builder.featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .featuresToDisable(MapperFeature.ALLOW_COERCION_OF_SCALARS, DeserializationFeature.ACCEPT_FLOAT_AS_INT)
            .postConfigurer(mapper -> mapper.coercionConfigFor(LocalDate.class)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull));
    }
    @Bean
    WebMvcConfigurer cors(@Value("${app.cors-origins:http://localhost:4200}") String origins) {
        String[] allowed = Arrays.stream(origins.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        return new WebMvcConfigurer() {
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v1/**").allowedOrigins(allowed)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("Content-Type", "If-Match", "Authorization")
                    .exposedHeaders("ETag", "Location").maxAge(3600);
            }
        };
    }
}
