package com.fourmint.reference.domain;
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

    public record AuditEntry(
        String at,
        String user,
        String action,
        String from,
        String to,
        String note,
        List<Change> changes
    ) {
        public AuditEntry {
            changes = list(changes);
        }
    }

    public record User(
        String id,
        String name,
        String role,
        String group,
        String initials,
        Boolean compliance
    ) implements Resource {
        public User {
            com.fourmint.platform.Checks.id(id);
            required(name, "name");
            required(role, "role");
        }
    }

    public record LegalEntity(
        String id,
        String name,
        String role,
        String country,
        String doc
    ) implements Resource {
        public LegalEntity {
            com.fourmint.platform.Checks.id(id);
            required(name, "name");
            oneOf(role, "role", "ProcessingOrg", "CounterParty");
        }
    }

    public record Sdi(
        String id,
        String entity,
        String ccy,
        String method,
        String account,
        String status,
        String reason,
        List<String> products,
        String type,
        LocalDate validFrom,
        LocalDate validTo,
        List<AuditEntry> audit
    ) implements Resource {
        public Sdi {
            com.fourmint.platform.Checks.id(id);
            products = list(products);
            audit = list(audit);
            required(entity, "entity");
            currency(ccy, "ccy");
            oneOf(status, "status", "Active", "Invalid");
            oneOf(type, "type", "CASH");
            required(validFrom, "validFrom");
            required(validTo, "validTo");
            if (validFrom.isAfter(validTo)) throw new IllegalArgumentException("validFrom must not follow validTo");
        }
    }

    public record Book(
        String id,
        String desc,
        String desk,
        String owner,
        String po
    ) implements Resource {
        public Book {
            com.fourmint.platform.Checks.id(id);
            required(po, "po");
        }
    }

    public record ProductDef(
        String id,
        String type,
        String pricer,
        String calendar,
        String settlement
    ) implements Resource {
        public ProductDef {
            com.fourmint.platform.Checks.id(id);
            oneOf(type, "type", "FXNDF", "Swap", "FXOption");
        }
    }

    public record HolidayCalendar(
        String id,
        List<LocalDate> holidays
    ) implements Resource {
        public HolidayCalendar {
            com.fourmint.platform.Checks.id(id);
            holidays = list(holidays);
        }
    }

}
