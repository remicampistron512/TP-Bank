package bank.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class Operation {
     private Long id;
     private LocalDateTime timestamp;
     private BigDecimal amount;
     private OperationType type;
     private OperationStatus status;


    public Operation(Long id, LocalDateTime timestamp, BigDecimal amount, OperationType type, OperationStatus status) {
        this.id = id;
        this.timestamp = timestamp;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }
}
