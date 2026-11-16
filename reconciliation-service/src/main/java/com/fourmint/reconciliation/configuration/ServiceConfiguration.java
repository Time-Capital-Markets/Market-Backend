package com.fourmint.reconciliation.configuration;
import com.fourmint.reconciliation.domain.Models.*;
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
    CrudUseCase<Counterparty> counterpartyUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "counterparties", Counterparty.class));
    }
    @Bean
    CrudUseCase<InternalSource> internalSourceUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "internal-sources", InternalSource.class));
    }
    @Bean
    CrudUseCase<SourceRecord> sourceRecordUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "records", SourceRecord.class));
    }
    @Bean
    CrudUseCase<MatchResult> matchResultUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "results", MatchResult.class));
    }
    @Bean
    CrudUseCase<ImportedFile> importedFileUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "files", ImportedFile.class));
    }
    @Bean
    CrudUseCase<PendingFile> pendingFileUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "pending-files", PendingFile.class));
    }
    @Bean
    CrudUseCase<ReconCycle> reconCycleUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "cycles", ReconCycle.class));
    }
    @Bean
    CrudUseCase<ReconEventRecord> reconEventRecordUseCase(JdbcTemplate jdbc, ObjectMapper mapper) {
        return new CrudService<>(new JdbcResourceRepository<>(jdbc, mapper, "events", ReconEventRecord.class));
    }
}
