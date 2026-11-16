package com.fourmint.reconciliation.configuration;
import com.fourmint.reconciliation.domain.Models.*;
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
            Map.entry("counterparties", Counterparty.class),
            Map.entry("internal-sources", InternalSource.class),
            Map.entry("records", SourceRecord.class),
            Map.entry("results", MatchResult.class),
            Map.entry("files", ImportedFile.class),
            Map.entry("pending-files", PendingFile.class),
            Map.entry("cycles", ReconCycle.class),
            Map.entry("events", ReconEventRecord.class)
        ));
    }
}
