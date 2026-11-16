package com.fourmint.marketdata.configuration;
import com.fasterxml.jackson.annotation.*;
import com.fourmint.marketdata.domain.Models.Point;
import org.springframework.context.annotation.*;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
@Configuration(proxyBeanMethods = false)
public class PointJsonConfiguration {
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"key", "value"})
    private abstract static class PointTuple {}
    @Bean
    Jackson2ObjectMapperBuilderCustomizer pointTuples() { return builder -> builder.mixIn(Point.class, PointTuple.class); }
}
