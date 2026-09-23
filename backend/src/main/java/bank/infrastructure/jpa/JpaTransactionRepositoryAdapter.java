package bank.infrastructure.jpa;

import bank.domain.Transaction;
import bank.service.TransactionPage;
import bank.service.TransactionQuery;
import bank.service.port.TransactionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public TransactionPage findPageByAccountId(UUID accountId, TransactionQuery query) {
        Specification<TransactionEntity> specification = (root, ignored, builder) -> builder.equal(root.get("accountId"), accountId);
        if (query.type() != null) {
            specification = specification.and((root, ignored, builder) -> builder.equal(root.get("type"), query.type()));
        }
        if (!query.text().isBlank()) {
            String text = "%" + query.text().toLowerCase(java.util.Locale.ROOT) + "%";
            specification = specification.and((root, ignored, builder) -> builder.like(builder.lower(root.get("description")), text));
        }
        if (query.from() != null) {
            specification = specification.and((root, ignored, builder) -> builder.greaterThanOrEqualTo(root.get("timestamp"), query.from()));
        }
        if (query.to() != null) {
            specification = specification.and((root, ignored, builder) -> builder.lessThanOrEqualTo(root.get("timestamp"), query.to()));
        }
        var transactionPage = repository.findAll(specification,
                PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "timestamp").and(Sort.by(Sort.Direction.DESC, "id"))));
        return new TransactionPage(transactionPage.getContent().stream()
                .map(TransactionEntity::toDomain)
                .toList(), query.page(), query.size(), transactionPage.getTotalElements());
    }
}
