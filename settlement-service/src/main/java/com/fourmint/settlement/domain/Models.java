package com.fourmint.settlement.domain;
import com.fourmint.platform.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static com.fourmint.platform.Checks.*;
/** Domain data and invariants; no Spring, HTTP, JDBC or Jackson dependencies. */
public final class Models {
    private Models() {}
    public record Transfer(
        String id,
        String trade,
        String type,
        BigDecimal amount,
        String estimate,
        String ccy,
        LocalDate valueDate,
        String method,
        String sdi,
        String status,
        boolean needs4Eyes,
        List<String> approvals,
        String flowId
    ) implements Resource {
        public Transfer {
            com.fourmint.platform.Checks.id(id);
            approvals = list(approvals);
            required(trade, "trade");
            oneOf(type, "type", "PAYMENT", "RECEIPT", "FEE");
            required(amount, "amount");
            currency(ccy, "ccy");
            required(valueDate, "valueDate");
            oneOf(status, "status", "PENDING", "VERIFIED", "SETTLED", "CANCELED");
        }
    }

    public record Message(
        String id,
        String trade,
        String type,
        String format,
        String to,
        String status,
        String at,
        String subject,
        String body,
        String sentBy
    ) implements Resource {
        public Message {
            com.fourmint.platform.Checks.id(id);
            required(trade, "trade");
            oneOf(type, "type", "CONFIRMATION", "EMAIL");
            oneOf(status, "status", "ACKED", "WAITING_ACK", "SENT");
        }
    }

}
