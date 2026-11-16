package com.fourmint.reconciliation.domain;
import com.fourmint.platform.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static com.fourmint.platform.Checks.*;
/** Domain data and invariants; no Spring, HTTP, JDBC or Jackson dependencies. */
public final class Models {
    private Models() {}
    public record Netting(
        String reference,
        List<String> transferIds,
        String reason
    ) {
        public Netting {
            transferIds = list(transferIds);
        }
    }

    public record FieldDiff(
        String field,
        String calypso,
        String broker,
        String diff,
        String rule,
        boolean ok,
        boolean exact
    ) {
    }

    public record Resolution(
        String reason,
        String comment
    ) {
    }

    public record AiDecision(
        String candidate,
        BigDecimal score,
        String action,
        String at,
        String actor
    ) {
        public AiDecision {
            oneOf(action, "action", "DISMISSED", "KEPT", "CONFIRMED");
        }
    }

    public record ReconEvent(
        String at,
        String actor,
        String action,
        String from,
        String to,
        String reason,
        String comment,
        String recordId,
        String detail
    ) {
    }

    public record PreviousCycle(
        LocalDate date,
        String label,
        BigDecimal matchRate,
        int breaks,
        BigDecimal exposureUsd
    ) {
    }

    public record Counterparty(
        String id,
        String name,
        String connectionType,
        boolean enabled,
        List<String> fileTypes,
        String schedule,
        String inbound,
        String lastReceivedAt,
        String health,
        String secretRef
    ) implements Resource {
        public Counterparty {
            com.fourmint.platform.Checks.id(id);
            fileTypes = list(fileTypes);
            required(name, "name");
            oneOf(health, "health", "OK", "DELAYED", "FAILED", "DISABLED");
        }
    }

    public record InternalSource(
        String id,
        String name,
        String connectionType,
        String schedule,
        String lastReceivedAt,
        String health
    ) implements Resource {
        public InternalSource {
            com.fourmint.platform.Checks.id(id);
            oneOf(health, "health", "OK", "DELAYED", "FAILED", "DISABLED");
        }
    }

    public record SourceRecord(
        String id,
        String source,
        String fileId,
        String sourceRecordId,
        String counterparty,
        LocalDate businessDate,
        String recordType,
        String subtype,
        String securityId,
        String transferId,
        String side,
        LocalDate settleDate,
        String ccy,
        BigDecimal quantity,
        BigDecimal amount,
        BigDecimal price,
        BigDecimal accrued,
        BigDecimal netProceeds,
        String extRef,
        String ingestedAt,
        Map<String,String> raw,
        String tradeId,
        String settlementGroup,
        Netting netting
    ) implements Resource {
        public SourceRecord {
            com.fourmint.platform.Checks.id(id);
            raw = map(raw);
            required(source, "source");
            required(counterparty, "counterparty");
            oneOf(recordType, "recordType", "CASH", "SECURITIES");
            oneOf(side, "side", "PAY", "RECEIVE", "BUY", "SELL", "DELIVER");
            currency(ccy, "ccy");
            required(amount, "amount");
            required(settleDate, "settleDate");
        }
    }

    public record MatchResult(
        String id,
        String key,
        List<String> records,
        String matchType,
        String status,
        String reviewReason,
        List<FieldDiff> diffs,
        String ruleVersion,
        String owner,
        String priority,
        boolean escalated,
        BigDecimal exposureUsd,
        String created,
        String updated,
        Resolution resolution,
        List<AiDecision> ai,
        List<ReconEvent> history
    ) implements Resource {
        public MatchResult {
            com.fourmint.platform.Checks.id(id);
            records = list(records);
            diffs = list(diffs);
            ai = list(ai);
            history = list(history);
            oneOf(matchType, "matchType", "EXACT", "TOLERANCE", "REFERENCE", "COMPOSITE", "AI_ASSISTED", "MANUAL", "NONE");
            oneOf(status, "status", "MATCHED", "TOLERANCE", "BREAK", "UNMATCHED_CALYPSO", "UNMATCHED_BROKER", "REVIEW", "PENDING", "RESOLVED");
            oneOf(priority, "priority", "HIGH", "MEDIUM", "LOW");
            nonNegative(exposureUsd, "exposureUsd");
        }
    }

    public record ImportedFile(
        String id,
        String source,
        String name,
        String receivedAt,
        int records
    ) implements Resource {
        public ImportedFile {
            com.fourmint.platform.Checks.id(id);
            required(name, "name");
            if (records < 0) throw new IllegalArgumentException("records cannot be negative");
        }
    }

    public record PendingFile(
        String id,
        String source,
        String name,
        List<Map<String,String>> rows
    ) implements Resource {
        public PendingFile {
            com.fourmint.platform.Checks.id(id);
            rows = list(rows);
            required(name, "name");
        }
    }

    public record ReconCycle(
        String id,
        LocalDate date,
        String label,
        String startedAt,
        String lastRunAt,
        String lastUpdated,
        BigDecimal progress,
        PreviousCycle previous
    ) implements Resource {
        public ReconCycle {
            com.fourmint.platform.Checks.id(id);
            required(date, "date");
            nonNegative(progress, "progress");
            if (progress.compareTo(BigDecimal.valueOf(100)) > 0) throw new IllegalArgumentException("progress must be <= 100");
        }
    }

    public record ReconEventRecord(
        String id,
        String at,
        String actor,
        String action,
        String from,
        String to,
        String reason,
        String comment,
        String recordId,
        String detail
    ) implements Resource {
        public ReconEventRecord {
            com.fourmint.platform.Checks.id(id);
            required(action, "action");
        }
    }

}
