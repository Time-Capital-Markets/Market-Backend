package com.fourmint.trading.domain;
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

    public record CashFlow(
        String id,
        String type,
        BigDecimal amount,
        String ccy,
        LocalDate date,
        BigDecimal fixing
    ) {
        public CashFlow {
            oneOf(type, "type", "PAYMENT", "RECEIPT");
            required(amount, "amount");
            currency(ccy, "ccy");
            required(date, "date");
        }
    }

    public record Trade(
        String id,
        String product,
        String productDef,
        String underlying,
        String direction,
        String cpty,
        String po,
        String book,
        BigDecimal notional,
        String ccy,
        BigDecimal rate,
        LocalDate tradeDate,
        LocalDate fixingDate,
        LocalDate settleDate,
        String fixingSource,
        String settleCcy,
        String status,
        String enteredBy,
        String authorizedBy,
        List<AuditEntry> audit,
        String sdi,
        String settlementMethod,
        List<CashFlow> cashflows
    ) implements Resource {
        public Trade {
            com.fourmint.platform.Checks.id(id);
            audit = list(audit);
            cashflows = list(cashflows);
            oneOf(product, "product", "FXNDF", "Swap", "FXOption");
            oneOf(status, "status", "PRICING", "PENDING", "VERIFIED", "CANCELED", "EXERCISED", "TERMINATED", "MATURED");
            required(cpty, "cpty");
            required(book, "book");
            nonNegative(notional, "notional");
            required(rate, "rate");
            currency(ccy, "ccy");
            currency(settleCcy, "settleCcy");
            required(tradeDate, "tradeDate");
            required(settleDate, "settleDate");
        }
    }

}
