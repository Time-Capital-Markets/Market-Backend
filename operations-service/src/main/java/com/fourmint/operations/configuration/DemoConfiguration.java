package com.fourmint.operations.configuration;
import com.fourmint.operations.domain.Models.*;
import com.fourmint.platform.spring.DemoSeeder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import java.util.Map;
@Configuration(proxyBeanMethods = false)
@Profile("demo")
public class DemoConfiguration {
    @Bean
    ApplicationRunner demoData(JdbcTemplate jdbc, ObjectMapper mapper, PlatformTransactionManager transactions) {
        return args -> new DemoSeeder(jdbc, mapper, new TransactionTemplate(transactions)).seed("fourmint-demo-v1", Map.ofEntries(
            Map.entry("schedules", ScheduledTask.class),
            Map.entry("engines", Engine.class),
            Map.entry("business-contexts", BusinessContext.class)
        ));
    }
}
