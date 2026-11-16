# FourMint Java backend

Seven independent Java 21 / Spring Boot 3.5.16 services providing typed CRUD APIs for the data represented by the sibling FourMint Angular prototype. The existing Node MIA/Gemini backend remains a separate service.

This first version implements data storage and retrieval. Trade authorization, four-eyes approval, reconciliation matching, scheduling execution, pricing, notifications and SLA calculations are not executed by these APIs. The Angular app still uses its localStorage stores until its clients are migrated.

## Services and ownership

| Service | Native/local port | API base | Owned resources |
| --- | --- | --- | --- |
| reference-service | 8081 | /api/v1/reference | users, entities, sdis, books, products, calendars |
| trading-service | 8082 | /api/v1/trading | trades (audit and cashflows embedded) |
| settlement-service | 8083 | /api/v1/settlement | transfers, messages |
| workflow-service | 8084 | /api/v1/workflow | tasks, sla-weeks, support-cases |
| market-data-service | 8085 | /api/v1/market-data | quotes, curves |
| operations-service | 8086 | /api/v1/operations | schedules, engines, business-contexts |
| reconciliation-service | 8087 | /api/v1/reconciliation | counterparties, internal-sources, records, results, files, pending-files, cycles, events |

Each service is its own executable JAR/container, migration history and database. Services refer to external aggregates by ID; they do not query one another's tables. There are no mandatory synchronous calls between services for this initial CRUD stage.

## Hexagonal architecture

~~~text
REST / JSON
    ↓
adapter/in/web/*Controller  (Spring HTTP adapter)
    ↓
CrudUseCase<T>              (input port)
    ↓
CrudService<T> + domain.Models  (application + domain rules)
    ↓
ResourceRepository<T>      (output port)
    ↓
JdbcResourceRepository<T>  (JDBC / Jackson adapter)
    ↓
Service-owned H2 or PostgreSQL database
~~~

- Each service owns its typed records and invariants in domain/Models.java. Domain code depends only on the JDK and platform-core; there are no Spring, Jackson or SQL annotations in these records.
- platform-core contains reusable CRUD ports, the application service, paging, versions and failures. It has no runtime framework dependencies.
- platform-spring contains reusable REST, JDBC, CORS and error adapters. The service's configuration/ package wires these adapters to the domain model.
- Controllers subclass the reusable adapter with a concrete domain type. Only data-independent mechanics are shared; there is no shared business-domain module or cross-service entity dependency.
- Nested audit entries, cashflows, quote observations and reconciliation details are part of their parent aggregate. Updating the parent replaces these collections.

Financial values use BigDecimal, not floating-point Java fields. Dates use LocalDate where the frontend uses calendar dates; existing timestamps and cutoff strings retain their frontend representation.

The initial persistence adapter stores validated typed aggregates as JSON text in a service-owned SQL table keyed by resource_type + id, with an optimistic version. Flyway manages the schema. This keeps the prototype's nested data intact; it does not provide relational reporting, foreign keys across services or arbitrary field filtering. The output port can later be implemented with domain-specific tables without changing the controllers' application interface.

## Build and run without Docker

Install JDK 21. The included Maven wrapper downloads Maven 3.9.11 on first use; no separate Maven installation is required. From this directory in PowerShell:

~~~powershell
.\mvnw.cmd verify
java -jar reference-service/target/reference-service-0.1.0-SNAPSHOT.jar --spring.profiles.active=demo
~~~

Run another JAR in another terminal, for example:

~~~powershell
java -jar trading-service/target/trading-service-0.1.0-SNAPSHOT.jar --spring.profiles.active=demo
~~~

Each service uses its own file-backed H2 database under ./data/ by default. Start commands from this repository root so that the location is predictable. Data survives process restarts. Omit --spring.profiles.active=demo for an initially empty database.

Every service exposes:

- GET /actuator/health (includes database connectivity).
- GET /actuator/health/liveness and /actuator/health/readiness.
- GET /openapi.json, an OpenAPI 3.0.3 contract with the service's endpoints and typed models. Import it into Postman, Insomnia or an OpenAPI viewer.

## Start all services with PostgreSQL

With Docker Engine and Compose installed:

~~~powershell
Copy-Item .env.example .env
# Set a local POSTGRES_PASSWORD in .env.
docker compose up --build -d
docker compose ps
docker compose logs -f trading-service
~~~

Compose creates seven databases in one local PostgreSQL server and binds the APIs to 127.0.0.1:8081 through 8087. It does not publish the database port. The SQL initialization runs only when the PostgreSQL volume is first created. This topology shares one database server for local convenience; use separate database credentials and access boundaries per service in a deployed environment.

Check http://localhost:8082/actuator/health and http://localhost:8082/openapi.json. Allow memory for seven JVMs plus PostgreSQL; about 6 GB available to Docker is a useful starting point.

~~~powershell
docker compose down
~~~

This stops containers and retains database data. The default Compose profile is demo, configurable in .env. Explicitly set SPRING_PROFILES_ACTIVE=default to start without demo import on a new database.

Build an individual image:

~~~powershell
docker build --build-arg SERVICE=trading-service -t fourmint-trading:local .
docker run --rm -p 8082:8080 -e SPRING_PROFILES_ACTIVE=demo fourmint-trading:local
~~~

Without a mounted /app/data volume or a PostgreSQL DB_URL, a standalone container's default H2 files last only for that container. Compose uses PostgreSQL volumes instead.

## API behavior

Every resource collection has these operations:

| Method | URL | Result |
| --- | --- | --- |
| GET | /api/v1/{domain}/{resource}?page=0&size=50 | Page of resources, sorted by ID |
| GET | /api/v1/{domain}/{resource}/{id} | Resource and ETag |
| POST | /api/v1/{domain}/{resource} | Create using body id; HTTP 201, Location and ETag |
| PUT | /api/v1/{domain}/{resource}/{id} | Replace existing resource; HTTP 200 and updated ETag |
| DELETE | /api/v1/{domain}/{resource}/{id} | HTTP 204 |

FourMint has IDs containing slashes and spaces, such as FXNDF USD/BRL. Use the query-id form for these IDs: GET, PUT and DELETE on /api/v1/reference/products?id=FXNDF%20USD%2FBRL. It also works for ordinary IDs. Do not rely on encoded slash support in path segments.

List responses have the shape:

~~~json
{
  "items": [],
  "page": 0,
  "size": 50,
  "hasNext": false
}
~~~

Page numbering starts at zero, size is between 1 and 200, and hasNext indicates whether to request the next page. There is no arbitrary filter or search API yet.

POST requires a caller-supplied unique ID within that resource collection. PUT is full replacement, not an upsert or a merge; send the complete resource. The body ID must match the requested ID. Use GET and retain its quoted ETag, then send If-Match: "1" with PUT/DELETE to prevent stale client updates. A successful PUT increments the version. If-Match is optional for initial integration; database writes still check the version read during that request.

Errors use application/problem+json: 400 for malformed JSON, unknown properties, invalid IDs, dates, enums, currencies and domain invariants; 404 for a missing item; 409 for duplicates or concurrent writes without an explicit version; 412 for stale If-Match. Unexpected errors return a generic 500 without exposing SQL details. See OpenAPI for required fields and the Java models for domain invariants.

Example in PowerShell:

~~~powershell
$Api = 'http://localhost:8081/api/v1/reference'
$Created = Invoke-WebRequest -Method Post -Uri "$Api/entities" -ContentType 'application/json' -Body '{"id":"DEMO-CP","name":"Demo Counterparty","role":"CounterParty","country":"BR","doc":"DEMO"}'
$Created.Headers.ETag
Invoke-RestMethod "$Api/entities/DEMO-CP"

Invoke-WebRequest -Method Put -Uri "$Api/entities/DEMO-CP" -Headers @{ 'If-Match' = '"1"' } -ContentType 'application/json' -Body '{"id":"DEMO-CP","name":"Updated Demo","role":"CounterParty","country":"BR","doc":"DEMO"}'
Invoke-WebRequest -Method Delete -Uri "$Api/entities/DEMO-CP" -Headers @{ 'If-Match' = '"2"' }
~~~

## Demo data and mapping to FourMint

The committed demo JSON files contain only the fictitious records from FourMint's seed.ts, market-data initialization, recon-seed.ts and simulated Salesforce fixtures. The demo profile imports each service's dataset in one transaction, once per database, using a seed_history marker. Existing records are preserved; changing a seed file does not overwrite already seeded databases or restore records deleted through the API. Use a new database for a fresh demonstration.

The optional development command below refreshes the committed snapshots from a sibling checkout with its npm dependencies installed:

~~~powershell
node scripts/export-demo.mjs ../FourMint
~~~

This export requires Node and the frontend's TypeScript dependency; Java builds and runtime do not. Review seed changes and rerun .\mvnw.cmd verify after exporting. The exported reconciliation results are computed once by the existing frontend matching function during export; the Java APIs do not run matching.

Frontend mappings are documented in [docs/frontend-mapping.md](docs/frontend-mapping.md). Important adaptations:

- SlaWeek, reconciliation events, calendars and business context gain an explicit stable id.
- Original field names and status spellings are preserved.
- Quote/curve points retain their existing two-element JSON tuples.
- The top-level di1 array is represented by the DI1 curve's process-date snapshot.
- Browser theme, accent, active user selection, counters and local schema version remain client concerns.

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| PORT | Service's native port (8081–8087); Docker sets 8080 | Listen port |
| DB_URL | jdbc:h2:file:./data/{domain};MODE=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE;WRITE_DELAY=0 | Use jdbc:postgresql://host:5432/database for PostgreSQL |
| DB_USERNAME | sa | Database login |
| DB_PASSWORD | empty | Database password |
| SPRING_PROFILES_ACTIVE | none (Compose defaults to demo) | Enable demo import explicitly |
| CORS_ALLOWED_ORIGINS | http://localhost:4200 | Comma-separated exact frontend origins |

No Gemini key is needed by these data services. Keep the AI provider key in the separate MIA backend. Local .env files are ignored and Compose reads them; native Java runs use process environment variables or --spring.* arguments, not automatic .env loading.

## Frontend and AWS integration

This repository creates the data APIs and does not switch the Angular stores away from localStorage. Replace Store/ReconStore reads and writes with HTTP clients incrementally; unwrap page.items and follow hasNext. Keep financial precision in mind when passing BigDecimal JSON numbers through JavaScript number values.

A future CloudFront/ALB setup can route /api/v1/reference/*, /api/v1/trading/* and the other service prefixes to distinct Fargate target groups. Keep /api/mia/* routed to the existing Node backend and the default behavior routed to S3. Preserve ETag and If-Match headers and disable API caching. Services shut down gracefully; use an ECS stop timeout of 120 seconds and health checks against /actuator/health/readiness.

These are CRUD prototype services without authentication, authorization, cross-service referential checks or durable messaging. The local Compose bindings are loopback-only. Before publishing the data APIs, add access control through your gateway/application and use service-owned database credentials and managed secrets.

## Validation

.\mvnw.cmd verify (or ./mvnw verify on Unix) compiles and packages all services and runs shared HTTP contract tests against each service using a migrated in-memory H2 database and the real demo datasets. Tests cover each of the 25 resources: creation, lookup and Location, replacement, deletion, pagination boundaries, duplicate IDs, unknown/missing input fields, ETags, stale/concurrent writers, CORS, health, OpenAPI and IDs containing slashes/spaces.

PostgreSQL runs through Compose; integration tests use H2 in PostgreSQL compatibility mode. A successful H2 test run does not replace a PostgreSQL deployment smoke test. Docker builds require a Docker installation.

See [docs/validation.md](docs/validation.md) for the completed validation record.

Spring requirements reference: [Spring Boot 3.5 system requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html).
