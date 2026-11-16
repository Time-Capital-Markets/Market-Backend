package com.fourmint.platform.spring;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigDecimal;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
@org.springframework.test.context.TestPropertySource(properties = "logging.level.root=WARN")
public abstract class AbstractCrudContract {
    @Autowired protected TestRestTemplate http;
    @Autowired protected ObjectMapper mapper;
    @Autowired private org.springframework.jdbc.core.JdbcTemplate jdbc;
    @Autowired @org.springframework.beans.factory.annotation.Qualifier("demoData")
    private org.springframework.boot.ApplicationRunner demoImporter;
    protected abstract String domain();
    private JsonNode seed() throws Exception {
        try (var input = new ClassPathResource("demo/seed.json").getInputStream()) { return mapper.readTree(input); }
    }
    private String collection(String resource) { return "/api/v1/" + domain() + "/" + resource; }
    private String item(String collection, String id) { return collection + "?id=" + URLEncoder.encode(id, StandardCharsets.UTF_8); }
    private ResponseEntity<String> request(String path, HttpMethod method, JsonNode value, String etag) {
        var headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
        if (etag != null) headers.set("If-Match", etag);
        // URI prevents TestRestTemplate from double-encoding query IDs containing "/" or spaces.
        URI uri = URI.create(http.getRootUri() + path);
        return http.exchange(uri, method, new HttpEntity<>(value == null ? null : value.toString(), headers), String.class);
    }
    @Test
    void everyResourceSupportsCrudValidationAndVersioning() throws Exception {
        var fields = seed().fields();
        while (fields.hasNext()) {
            var entry = fields.next();
            assertFalse(entry.getValue().isEmpty(), "Missing representative seed for " + entry.getKey());
            String url = collection(entry.getKey());
            var existing = request(url + "?size=1", HttpMethod.GET, null, null);
            assertEquals(200, existing.getStatusCode().value(), url);
            var page = mapper.readTree(existing.getBody());
            assertEquals(1, page.path("items").size(), url);
            assertEquals(0, page.path("page").asInt());
            assertEquals(400, request(url + "?size=201", HttpMethod.GET, null, null).getStatusCode().value());
            assertEquals(400, request(url + "?page=-1", HttpMethod.GET, null, null).getStatusCode().value());
            ObjectNode sample = entry.getValue().get(0).deepCopy();
            sample.put("id", "contract-" + entry.getKey() + "/with space");
            if (sample.has("notional")) sample.put("notional", new BigDecimal("9999999999999999.123456789"));
            String path = item(url, sample.path("id").asText());
            var create = request(url, HttpMethod.POST, sample, null);
            assertEquals(201, create.getStatusCode().value(), url + " " + create.getBody());
            if (sample.has("notional")) assertTrue(create.getBody().contains("9999999999999999.123456789"), "Financial decimal precision must survive storage");
            assertEquals("\"1\"", create.getHeaders().getETag());
            assertNotNull(create.getHeaders().getLocation());
            var located = http.getForEntity(create.getHeaders().getLocation(), String.class);
            assertEquals(200, located.getStatusCode().value(), "Location must resolve");
            assertEquals(mapper.readTree(located.getBody()), mapper.readTree(create.getBody()), "Read/write fidelity");
            assertEquals(409, request(url, HttpMethod.POST, sample, null).getStatusCode().value());
            ObjectNode invalid = sample.deepCopy(); invalid.put("unexpectedField", true);
            assertEquals(400, request(url, HttpMethod.POST, invalid, null).getStatusCode().value());
            invalid = sample.deepCopy(); invalid.remove("id");
            assertEquals(400, request(url, HttpMethod.POST, invalid, null).getStatusCode().value());
            if (sample.has("ccy")) {
                invalid = sample.deepCopy(); invalid.put("ccy", "usd");
                assertEquals(400, request(url, HttpMethod.POST, invalid, null).getStatusCode().value());
            }
            if (sample.has("status")) {
                invalid = sample.deepCopy(); invalid.put("status", "UNKNOWN_STATUS");
                // Support cases deliberately preserve free-form external status values.
                if (!entry.getKey().equals("support-cases")) assertEquals(400, request(url, HttpMethod.POST, invalid, null).getStatusCode().value());
            }
            if (sample.has("settleDate")) {
                invalid = sample.deepCopy(); invalid.put("settleDate", "2026-02-31");
                assertEquals(400, request(url, HttpMethod.POST, invalid, null).getStatusCode().value());
            }
            assertEquals(412, request(path, HttpMethod.PUT, sample, "\"999\"").getStatusCode().value());
            assertEquals(400, request(path, HttpMethod.PUT, sample, "1").getStatusCode().value());
            var updated = request(path, HttpMethod.PUT, sample, "\"1\"");
            assertEquals(200, updated.getStatusCode().value());
            assertEquals("\"2\"", updated.getHeaders().getETag());
            ObjectNode wrongId = sample.deepCopy(); wrongId.put("id", "wrong-id");
            assertEquals(400, request(path, HttpMethod.PUT, wrongId, null).getStatusCode().value());
            assertEquals(412, request(path, HttpMethod.DELETE, null, "\"1\"").getStatusCode().value());
            assertEquals(204, request(path, HttpMethod.DELETE, null, "\"2\"").getStatusCode().value());
            assertEquals(404, request(path, HttpMethod.GET, null, null).getStatusCode().value());
            assertEquals(404, request(path, HttpMethod.PUT, sample, null).getStatusCode().value());
            assertEquals(404, request(path, HttpMethod.DELETE, null, null).getStatusCode().value());
        }
    }
    @Test
    void importingDemoIntoAnExistingDatabasePreservesStoredVersions() throws Exception {
        var entry = seed().fields().next();
        var original = entry.getValue().get(0);
        String path = item(collection(entry.getKey()), original.path("id").asText());
        var current = request(path, HttpMethod.GET, null, null);
        var updated = request(path, HttpMethod.PUT, original, current.getHeaders().getETag());
        assertEquals(200, updated.getStatusCode().value());
        // Simulate enabling the demo profile after real CRUD data already exists.
        jdbc.update("DELETE FROM seed_history WHERE seed_key = ?", "fourmint-demo-v1");
        demoImporter.run(new org.springframework.boot.DefaultApplicationArguments(new String[0]));
        var preserved = request(path, HttpMethod.GET, null, null);
        assertEquals(updated.getHeaders().getETag(), preserved.getHeaders().getETag());
        assertEquals(mapper.readTree(updated.getBody()), mapper.readTree(preserved.getBody()));
    }
    @Test
    void concurrentWritersWithTheSameVersionCannotOverwriteEachOther() throws Exception {
        var entry = seed().fields().next();
        ObjectNode sample = entry.getValue().get(0).deepCopy(); sample.put("id", "concurrent-test");
        String url = collection(entry.getKey()), path = item(url, "concurrent-test");
        assertEquals(201, request(url, HttpMethod.POST, sample, null).getStatusCode().value());
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var start = new CountDownLatch(1);
            Callable<Integer> write = () -> { start.await(); return request(path, HttpMethod.PUT, sample, "\"1\"").getStatusCode().value(); };
            var first = executor.submit(write); var second = executor.submit(write); start.countDown();
            assertEquals(java.util.List.of(200, 412), java.util.stream.Stream.of(first.get(), second.get()).sorted().toList());
        } finally { request(path, HttpMethod.DELETE, null, null); }
    }
    @Test
    void healthOpenApiCorsAndSeededSpecialIdsAreReachable() throws Exception {
        assertEquals(200, http.getForEntity("/actuator/health", String.class).getStatusCode().value());
        var api = http.getForEntity("/openapi.json", String.class);
        assertEquals(200, api.getStatusCode().value());
        assertEquals("3.0.3", mapper.readTree(api.getBody()).path("openapi").asText());
        var entry = seed().fields().next();
        String path = item(collection(entry.getKey()), entry.getValue().get(0).path("id").asText());
        assertEquals(200, request(path, HttpMethod.GET, null, null).getStatusCode().value());
        // HttpURLConnection suppresses Access-Control-Request-* headers. Use the JDK HTTP client for real preflights.
        try (var client = HttpClient.newHttpClient()) {
            var preflight = HttpRequest.newBuilder(URI.create(http.getRootUri() + collection(entry.getKey())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://localhost:4200").header("Access-Control-Request-Method", "PUT")
                .header("Access-Control-Request-Headers", "If-Match,Content-Type").build();
            var cors = client.send(preflight, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, cors.statusCode(), cors.body());
            assertEquals("http://localhost:4200", cors.headers().firstValue("access-control-allow-origin").orElse(""));
        }
    }
}
