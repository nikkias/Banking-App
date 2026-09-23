package bank.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record AuditEvent(
        UUID id,
        String actor,
        String action,
        String resourceType,
        UUID resourceId,
        BigDecimal amount,
        Instant timestamp,
        String outcome,
        String description) {
    public AuditEvent {
        Objects.requireNonNull(id, "id");
        requireText(actor, "actor");
        requireText(action, "action");
        requireText(resourceType, "resourceType");
        Objects.requireNonNull(resourceId, "resourceId");
        Objects.requireNonNull(timestamp, "timestamp");
        requireText(outcome, "outcome");
        requireText(description, "description");
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
