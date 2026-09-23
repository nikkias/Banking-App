package bank.infrastructure.memory;

import bank.domain.AuditEvent;
import bank.service.port.AuditEventRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("memory")
public class InMemoryAuditEventRepository implements AuditEventRepository {
    private final List<AuditEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public AuditEvent save(AuditEvent event) {
        events.add(event);
        return event;
    }

    @Override
    public List<AuditEvent> findAll() {
        return events.stream()
                .sorted(Comparator.comparing((AuditEvent event) -> event.timestamp()).reversed())
                .toList();
    }
}
