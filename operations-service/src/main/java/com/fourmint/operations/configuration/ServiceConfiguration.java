package com.fourmint.operations.configuration;
import com.fourmint.operations.domain.Models.*;
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
    CrudUseCase<ScheduledTask> scheduledTaskUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "schedules", ScheduledTask.class));
    }
    @Bean
    CrudUseCase<Engine> engineUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "engines", Engine.class));
    }
    @Bean
    CrudUseCase<BusinessContext> businessContextUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "business-contexts", BusinessContext.class));
    }
}
