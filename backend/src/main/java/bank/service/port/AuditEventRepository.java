package bank.service.port;

import bank.domain.AuditEvent;

import java.util.List;

public interface AuditEventRepository {
    AuditEvent save(AuditEvent event);

    List<AuditEvent> findAll();
}
