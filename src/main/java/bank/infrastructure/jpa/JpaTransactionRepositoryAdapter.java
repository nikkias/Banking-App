package bank.infrastructure.jpa;

import bank.domain.Transaction;
import bank.service.port.TransactionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
@Profile("postgres")
public class JpaTransactionRepositoryAdapter implements TransactionRepository {
    private final SpringDataTransactionRepository repository;

    public JpaTransactionRepositoryAdapter(SpringDataTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionEntity.fromDomain(Objects.requireNonNull(transaction, "transaction"));
        return repository.save(Objects.requireNonNull(entity, "entity")).toDomain();
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        return repository.findByAccountIdOrderByTimestampDesc(accountId).stream()
            .map(entity -> entity.toDomain())
                .toList();
    }
}
