package bank.infrastructure.jpa;

import bank.domain.AuditEvent;
import bank.service.port.AuditEventRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@Profile("postgres")
public class JpaAuditEventRepositoryAdapter implements AuditEventRepository {
    private final SpringDataAuditEventRepository repository;

    public JpaAuditEventRepositoryAdapter(SpringDataAuditEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditEvent save(AuditEvent event) {
        AuditEventEntity entity = AuditEventEntity.fromDomain(Objects.requireNonNull(event, "event"));
        return repository.save(Objects.requireNonNull(entity, "entity")).toDomain();
    }

    @Override
    public List<AuditEvent> findAll() {
        return repository.findAllByOrderByTimestampDesc().stream()
                .map(entity -> entity.toDomain())
                .toList();
    }
}
