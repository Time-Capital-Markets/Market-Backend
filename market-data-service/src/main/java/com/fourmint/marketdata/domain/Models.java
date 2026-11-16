package com.fourmint.marketdata.domain;
import com.fourmint.platform.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static com.fourmint.platform.Checks.*;
/** Domain data and invariants; no Spring, HTTP, JDBC or Jackson dependencies. */
public final class Models {
    private Models() {}
    public record Point(
        String key,
        BigDecimal value
    ) {
        public Point {
            required(key, "key");
            required(value, "value");
        }
    }

    public record QuoteObservation(
        LocalDate date,
        String type,
        BigDecimal value
    ) {
        public QuoteObservation {
            required(date, "date");
            oneOf(type, "type", "Bid", "Ask", "Close");
            required(value, "value");
        }
    }

    public record CurveSnapshot(
        LocalDate date,
        List<Point> points
    ) {
        public CurveSnapshot {
            points = list(points);
            required(date, "date");
        }
    }

    public record QuoteSeries(
        String id,
        String label,
        String unit,
        List<Point> points,
        List<QuoteObservation> observations
    ) implements Resource {
        public QuoteSeries {
            com.fourmint.platform.Checks.id(id);
            points = list(points);
            observations = list(observations);
            required(label, "label");
        }
    }

    public record MarketCurve(
        String id,
        String label,
        String unit,
        List<CurveSnapshot> snapshots
    ) implements Resource {
        public MarketCurve {
            com.fourmint.platform.Checks.id(id);
            snapshots = list(snapshots);
            required(label, "label");
        }
    }

}
