package bank.infrastructure.jpa;

import bank.domain.AuditEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
class AuditEventEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String actor;

    @Column(nullable = false)
    private String action;

    @Column(name = "resource_type", nullable = false)
    private String resourceType;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private String outcome;

    @Column(nullable = false)
    private String description;

    protected AuditEventEntity() {
    }

    private AuditEventEntity(AuditEvent event) {
        this.id = event.id();
        this.actor = event.actor();
        this.action = event.action();
        this.resourceType = event.resourceType();
        this.resourceId = event.resourceId();
        this.amount = event.amount();
        this.timestamp = event.timestamp();
        this.outcome = event.outcome();
        this.description = event.description();
    }

    static AuditEventEntity fromDomain(AuditEvent event) {
        return new AuditEventEntity(event);
    }

    AuditEvent toDomain() {
        return new AuditEvent(id, actor, action, resourceType, resourceId, amount, timestamp, outcome, description);
    }
}
