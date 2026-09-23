package bank.infrastructure.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

@Profile("postgres")
interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, UUID>, JpaSpecificationExecutor<TransactionEntity> {
    List<TransactionEntity> findByAccountIdOrderByTimestampDesc(UUID accountId);

    Page<TransactionEntity> findByAccountId(UUID accountId, Pageable pageable);
}
