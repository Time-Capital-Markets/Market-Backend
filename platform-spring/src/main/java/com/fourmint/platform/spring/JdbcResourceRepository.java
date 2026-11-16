package com.fourmint.platform.spring;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourmint.platform.*;
import com.fourmint.platform.port.out.ResourceRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;
public final class JdbcResourceRepository<T extends Resource> implements ResourceRepository<T> {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final String resourceType;
    private final Class<T> type;
    public JdbcResourceRepository(JdbcTemplate jdbc, ObjectMapper mapper, String resourceType, Class<T> type) {
        this.jdbc = jdbc; this.mapper = mapper; this.resourceType = resourceType; this.type = type;
    }
    public Optional<Versioned<T>> find(String id) {
        return jdbc.query("SELECT payload, version FROM resources WHERE resource_type = ? AND id = ?",
            (rs, row) -> new Versioned<>(decode(rs.getString("payload")), rs.getLong("version")), resourceType, id).stream().findFirst();
    }
    public List<T> findPage(long offset, int limit) {
        return jdbc.query("SELECT payload FROM resources WHERE resource_type = ? ORDER BY id LIMIT ? OFFSET ?",
            (rs, row) -> decode(rs.getString("payload")), resourceType, limit, offset);
    }
    public boolean insert(T value) {
        try {
            jdbc.update("INSERT INTO resources (resource_type, id, payload, version) VALUES (?, ?, ?, 1)", resourceType, value.id(), encode(value));
            return true;
        } catch (DuplicateKeyException duplicate) { return false; }
    }
    public boolean replace(T value, long version) {
        return jdbc.update("UPDATE resources SET payload = ?, version = version + 1 WHERE resource_type = ? AND id = ? AND version = ?",
            encode(value), resourceType, value.id(), version) == 1;
    }
    public boolean delete(String id, long version) {
        return jdbc.update("DELETE FROM resources WHERE resource_type = ? AND id = ? AND version = ?", resourceType, id, version) == 1;
    }
    private String encode(T value) {
        try { return mapper.writeValueAsString(value); }
        catch (JsonProcessingException error) { throw new IllegalStateException("Cannot serialize resource", error); }
    }
    private T decode(String json) {
        try { return mapper.readValue(json, type); }
        catch (JsonProcessingException error) { throw new IllegalStateException("Cannot deserialize stored resource", error); }
    }
}
