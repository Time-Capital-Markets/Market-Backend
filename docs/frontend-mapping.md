# Frontend ownership and integration map

Source: sibling FourMint/src/app/core/models.ts and recon-models.ts. The Java records are typed data contracts for an initial CRUD stage.

| Frontend state field | Owner / collection | Mapping |
| --- | --- | --- |
| users | reference / users | Same fields; user records do not implement identity/authentication |
| entities | reference / entities | LegalEntity |
| sdis | reference / sdis | SDI with audit embedded |
| books | reference / books | Same fields |
| products | reference / products | IDs may contain slash; use query-id operations |
| calendar.BRL, calendar.reference | reference / calendars | {id: "BRL" or "reference", holidays: [...]} |
| trades | trading / trades | Trade including optional SDI, settlement method, audit and cashflows |
| transfers | settlement / transfers | Same fields including approvals |
| messages | settlement / messages | Data records; CRUD does not send an email or SWIFT message |
| tasks | workflow / tasks | Same fields including pending changes, history and reconciliation context |
| sla | workflow / sla-weeks | Adds id equal to week; raw aggregates, not computed reports |
| simulated Salesforce cases | workflow / support-cases | Snapshot of existing fixture; no Salesforce connector |
| quotes | market-data / quotes | Points remain [date, value] tuples |
| curves | market-data / curves | Snapshots and points retained |
| di1 | market-data / curves?id=DI1 | points on process-date snapshot |
| schedules | operations / schedules | State/configuration only; no scheduler |
| engines | operations / engines | State/configuration only; does not launch a process |
| processDate, lockDate, entityLabel | operations / business-contexts?id=default | Shared operational context |
| recon.counterparties | reconciliation / counterparties | Only secretRef, never actual credentials |
| recon.calypso | reconciliation / internal-sources?id=CALYPSO | Internal source |
| recon.records | reconciliation / records | SourceRecord, raw data and netting retained |
| recon.results | reconciliation / results | MatchResult including diffs, history, AI decisions and resolution |
| recon.files | reconciliation / files | Received-file metadata |
| recon.pendingFiles | reconciliation / pending-files | Pending rows embedded; no ingestion execution |
| recon.cycle and recon.previous | reconciliation / cycles | id = cycle date, previous embedded |
| recon.events | reconciliation / events | Adds stable id |
| theme, accent, me, v, seq | browser | UI preferences, session selection and local implementation state |

Base URLs are /api/v1/{domain}/{collection}. Lists are paginated envelopes; individual reads return the record itself with an ETag header. Optional absent lists/maps normalize to empty collections, and optional scalar properties can serialize as null. Blank date strings, such as a Swap's unused fixingDate, normalize to null; required dates still reject blanks. UI consumers should tolerate either absent or null optional fields during migration.

A PUT can currently change a status as ordinary data. Domain workflow commands should be added as separate input ports when moving authorization, four-eyes, matching, audit ownership and side effects to the server. Do not equate a status field update with a completed business process.

Cross-service references remain string IDs. Deletes do not cascade, and CRUD does not guarantee referenced trades, books, users or counterparties exist. Frontend demo inconsistencies are intentionally preserved rather than fabricating foreign-key relationships.

The browser's local seq counters are unsuitable for concurrent clients. Use globally unique caller-generated IDs for new resources until a server-owned ID policy is added; existing demo IDs remain valid.
