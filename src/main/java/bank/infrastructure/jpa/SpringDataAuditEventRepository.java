package bank.infrastructure.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

@Profile("postgres")
interface SpringDataAuditEventRepository extends JpaRepository<AuditEventEntity, UUID> {
    List<AuditEventEntity> findAllByOrderByTimestampDesc();
}
