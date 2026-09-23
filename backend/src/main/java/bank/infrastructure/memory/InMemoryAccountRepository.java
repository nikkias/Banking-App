package bank.infrastructure.memory;

import bank.domain.Account;
import bank.service.port.AccountRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("memory")
public class InMemoryAccountRepository implements AccountRepository {
    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {
        accounts.put(account.id(), account);
        return account;
    }

    @Override
    public Optional<Account> findById(UUID accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    @Override
    public List<Account> findAll() {
        return accounts.values().stream()
            .sorted(Comparator.comparing(account -> account.iban()))
                .toList();
    }

    @Override
    public long count() {
        return accounts.size();
    }
}
