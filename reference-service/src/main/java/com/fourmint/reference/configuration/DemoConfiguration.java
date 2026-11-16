package com.fourmint.reference.configuration;
import com.fourmint.reference.domain.Models.*;
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
            Map.entry("users", User.class),
            Map.entry("entities", LegalEntity.class),
            Map.entry("sdis", Sdi.class),
            Map.entry("books", Book.class),
            Map.entry("products", ProductDef.class),
            Map.entry("calendars", HolidayCalendar.class)
        ));
    }
}
