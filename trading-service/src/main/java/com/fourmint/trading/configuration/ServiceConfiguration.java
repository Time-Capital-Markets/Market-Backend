package com.fourmint.trading.configuration;
import com.fourmint.trading.domain.Models.*;
import com.fourmint.platform.application.CrudService;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.*;
@Configuration(proxyBeanMethods = false)
@Import(PlatformConfiguration.class)
public class ServiceConfiguration {
    @Bean
    CrudUseCase<Trade> tradeUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "trades", Trade.class));
    }
}
