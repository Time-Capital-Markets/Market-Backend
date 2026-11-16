package com.fourmint.operations.domain;
import com.fourmint.platform.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static com.fourmint.platform.Checks.*;
/** Domain data and invariants; no Spring, HTTP, JDBC or Jackson dependencies. */
public final class Models {
    private Models() {}
    public record ScheduledTask(
        String id,
        String name,
        String type,
        String time,
        String freq,
        String last,
        String lastRun,
        boolean enabled,
        String error
    ) implements Resource {
        public ScheduledTask {
            com.fourmint.platform.Checks.id(id);
            required(name, "name");
            oneOf(last, "last", "OK", "ERROR");
        }
    }

    public record Engine(
        String id,
        String label,
        String desc,
        String status,
        int backlog,
        List<BigDecimal> rate,
        String warn,
        List<String> config
    ) implements Resource {
        public Engine {
            com.fourmint.platform.Checks.id(id);
            rate = list(rate);
            config = list(config);
            required(label, "label");
            oneOf(status, "status", "RUNNING", "STOPPED");
            if (backlog < 0) throw new IllegalArgumentException("backlog cannot be negative");
        }
    }

    public record BusinessContext(
        String id,
        LocalDate processDate,
        LocalDate lockDate,
        String entityLabel
    ) implements Resource {
        public BusinessContext {
            com.fourmint.platform.Checks.id(id);
            required(processDate, "processDate");
            required(lockDate, "lockDate");
        }
    }

}
