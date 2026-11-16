package com.fourmint.workflow.domain;
import com.fourmint.platform.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static com.fourmint.platform.Checks.*;
/** Domain data and invariants; no Spring, HTTP, JDBC or Jackson dependencies. */
public final class Models {
    private Models() {}
    public record Change(
        String field,
        String before,
        String after
    ) {
    }

    public record TaskEvent(
        String at,
        String user,
        String kind,
        String text,
        List<Change> changes
    ) {
        public TaskEvent {
            changes = list(changes);
            oneOf(kind, "kind", "CREATED", "VIEWED", "TAKEN", "ACTION", "CHANGE", "COMPLETED", "BLOCKED");
        }
    }

    public record TaskRecon(
        String cycle,
        String broker,
        BigDecimal exposureUsd,
        String status,
        List<String> records
    ) {
        public TaskRecon {
            records = list(records);
        }
    }

    public record PendingChange(
        List<Change> changes,
        String requestedBy
    ) {
        public PendingChange {
            changes = list(changes);
        }
    }

    public record Task(
        String id,
        String type,
        String kind,
        String object,
        String objectType,
        String trade,
        String status,
        String priority,
        String group,
        String owner,
        TaskRecon recon,
        String created,
        String cutoff,
        String comment,
        List<TaskEvent> history,
        String body,
        String to,
        PendingChange pending,
        Boolean read,
        String series,
        LocalDate date
    ) implements Resource {
        public Task {
            com.fourmint.platform.Checks.id(id);
            history = list(history);
            oneOf(type, "type", "TRADE_PENDING", "TRANSFER_PENDING", "EX_MISSING_PRICE", "EX_MISSING_SDI", "EX_INVALID_SDI", "EX_MISSING_PRODUCT", "EX_RETRO_AMEND", "MSG_ACK_PENDING", "RECON_BREAK", "NOTIFICATION");
            oneOf(kind, "kind", "TASK", "NOTIFICATION");
            oneOf(objectType, "objectType", "Trade", "Transfer", "Quote", "Product", "Message", "ScheduledTask", "Handover", "ReconBreak");
            oneOf(status, "status", "NEW", "UNDER_PROCESSING", "COMPLETED");
            oneOf(priority, "priority", "HIGH", "MEDIUM", "LOW");
            required(object, "object");
        }
    }

    public record SlaWeek(
        String id,
        String week,
        LocalDate start,
        Map<String,Integer> breaches,
        Map<String,BigDecimal> resolvedMin,
        Map<String,Integer> total
    ) implements Resource {
        public SlaWeek {
            com.fourmint.platform.Checks.id(id);
            breaches = map(breaches);
            resolvedMin = map(resolvedMin);
            total = map(total);
            required(week, "week");
            required(start, "start");
            if (breaches.values().stream().anyMatch(v -> v < 0) || total.values().stream().anyMatch(v -> v < 0) || resolvedMin.values().stream().anyMatch(v -> v.signum() < 0)) throw new IllegalArgumentException("SLA metrics cannot be negative");
        }
    }

    public record SupportCase(
        String id,
        String subject,
        String category,
        String status,
        String priority,
        String service,
        LocalDate openedAt,
        LocalDate closedAt,
        List<String> taskTypes,
        List<String> series,
        String description,
        String resolution
    ) implements Resource {
        public SupportCase {
            com.fourmint.platform.Checks.id(id);
            taskTypes = list(taskTypes);
            series = list(series);
            required(subject, "subject");
            oneOf(category, "category", "Incident", "Training", "Question");
            required(openedAt, "openedAt");
        }
    }

}
