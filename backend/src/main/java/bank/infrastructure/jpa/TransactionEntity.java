package bank.infrastructure.jpa;

import bank.domain.Transaction;
import bank.domain.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
class TransactionEntity {
    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private String description;

    protected TransactionEntity() {
    }

    private TransactionEntity(UUID id, UUID accountId, TransactionType type, BigDecimal amount, Instant timestamp, String description) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
    }

    static TransactionEntity fromDomain(Transaction transaction) {
        return new TransactionEntity(
                transaction.id(),
                transaction.accountId(),
                transaction.type(),
                transaction.amount(),
                transaction.timestamp(),
                transaction.description());
    }

    Transaction toDomain() {
        return new Transaction(id, accountId, type, amount, timestamp, description);
    }
}
