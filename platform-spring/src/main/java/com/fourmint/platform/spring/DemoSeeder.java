package com.fourmint.platform.spring;
import com.fasterxml.jackson.databind.*;
import com.fourmint.platform.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import java.io.IOException;
import java.util.Map;
/** Demo-only, atomic, one-time dataset import. Deletions survive restarts. */
public final class DemoSeeder {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final TransactionTemplate transaction;
    public DemoSeeder(JdbcTemplate jdbc, ObjectMapper mapper, TransactionTemplate transaction) {
        this.jdbc = jdbc; this.mapper = mapper; this.transaction = transaction;
    }
    public void seed(String seedKey, Map<String, Class<? extends Resource>> types) throws IOException {
        final JsonNode data;
        try (var input = new ClassPathResource("demo/seed.json").getInputStream()) { data = mapper.readTree(input); }
        transaction.executeWithoutResult(status -> {
            if (!jdbc.queryForList("SELECT seed_key FROM seed_history WHERE seed_key = ?", String.class, seedKey).isEmpty()) return;
            types.forEach((name, type) -> insertAll(name, type, data.path(name)));
            jdbc.update("INSERT INTO seed_history (seed_key) VALUES (?)", seedKey);
        });
    }
    private <T extends Resource> void insertAll(String name, Class<T> type, JsonNode rows) {
        var repository = new JdbcResourceRepository<>(jdbc, mapper, name, type);
        for (var row : rows) {
            try {
                var value = mapper.treeToValue(row, type);
                // Avoid a duplicate-key statement aborting the PostgreSQL seed transaction.
                if (repository.find(value.id()).isEmpty()) repository.insert(value);
            }
            catch (IOException error) { throw new IllegalStateException("Invalid demo resource: " + name, error); }
        }
    }
}
