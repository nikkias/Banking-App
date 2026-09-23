package bank.infrastructure.jpa;

import bank.domain.Account;
import bank.service.port.AccountRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("postgres")
public class JpaAccountRepositoryAdapter implements AccountRepository {
    private final SpringDataAccountRepository repository;

    public JpaAccountRepositoryAdapter(SpringDataAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account save(Account account) {
        Account nonNullAccount = Objects.requireNonNull(account, "account");
        AccountEntity entity = repository.findById(nonNullAccount.id())
                .map(existing -> {
                    existing.updateFrom(nonNullAccount);
                    return existing;
                })
                .orElseGet(() -> AccountEntity.fromDomain(nonNullAccount));
        return repository.save(Objects.requireNonNull(entity, "entity")).toDomain();
    }

    @Override
    public Optional<Account> findById(UUID accountId) {
        return repository.findById(Objects.requireNonNull(accountId, "accountId")).map(entity -> entity.toDomain());
    }

    @Override
    public List<Account> findAll() {
        return repository.findAll().stream()
            .map(entity -> entity.toDomain())
            .sorted(Comparator.comparing(account -> account.iban()))
                .toList();
    }

    @Override
    public long count() {
        return repository.count();
    }
}
