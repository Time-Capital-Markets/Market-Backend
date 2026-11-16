package com.fourmint.workflow.configuration;
import com.fourmint.workflow.domain.Models.*;
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
    CrudUseCase<Task> taskUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "tasks", Task.class));
    }
    @Bean
    CrudUseCase<SlaWeek> slaWeekUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "sla-weeks", SlaWeek.class));
    }
    @Bean
    CrudUseCase<SupportCase> supportCaseUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "support-cases", SupportCase.class));
    }
}
