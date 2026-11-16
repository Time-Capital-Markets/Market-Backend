# Initial architecture decisions

1. Seven bounded contexts, seven executable services. Grouping follows operational ownership, not one process per table. Each service can be built and deployed independently.
2. Ports and domain stay independent of Spring. Configuration is the composition root; HTTP/JDBC/JSON belong in adapters. Generic CRUD is shared only while these use cases are identical.
3. Typed Java records preserve the prototype's payloads. Monetary amounts, rates and quantities use BigDecimal. References to another context are IDs, not shared Java domain entities.
4. Aggregate documents are stored in SQL with Flyway migrations. This is deliberately an initial storage adapter, not a final relational model. Each database has its own resources and seed_history tables; no service accesses another database.
5. Atomic compare-and-update/delete SQL prevents races. Optional HTTP If-Match supports clients that need stale-write protection. Multi-service transactions and event delivery are deferred.
6. H2 provides durable local startup with only Java. PostgreSQL is configured through standard datasource variables and exercised by the Compose topology; automated contracts run against H2.
7. Demo imports are opt-in outside Compose and run once per database. They preserve fictitious source records, including intentionally missing/invalid relationships used by FourMint demonstrations.
8. No gateway is required for independent local testing. A gateway/ALB can route the distinct API prefixes later, while the Node MIA path remains independent.
