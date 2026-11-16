# Validation record

Validated on 2026-11-12 using Eclipse Temurin JDK 21.0.13 and Maven 3.9.11 on Windows.

- Full Maven reactor verification succeeded: shared libraries and all seven executable service JARs.
- 28 HTTP integration tests passed, with zero failures/errors. The shared contract covers all 25 resource collections using real demo snapshots, migrated H2 databases and running HTTP servers.
- Tests include CRUD, pagination, input validation, precise financial decimals, special-character IDs, duplicate IDs, ETags, stale/concurrent updates, CORS, health, OpenAPI, and preserving existing data during a demo import.
- The market-data module was verified again after removing a redundant DI1 curve from the seed export.
- Demo export contains 191 records with unique IDs within each collection.
- A packaged trading JAR passed a file-backed database restart smoke test: API-created records remained, and deleted demo records were not restored by the next startup. Local H2 uses WRITE_DELAY=0 for synchronous commit logging.
- The Maven wrapper downloaded Maven successfully and verified its pinned distribution SHA-256.
- Compose/application YAML parsed successfully; OpenAPI documents, operation IDs and schema references were checked.
- Domain source dependency checks found no Spring, Jackson, JDBC or Jakarta imports; source credential checks found no local Gemini key or common credential patterns.

Docker is not installed in the validation environment. Container builds and the PostgreSQL Compose topology were not executed; automated database tests used H2 in PostgreSQL compatibility mode.
