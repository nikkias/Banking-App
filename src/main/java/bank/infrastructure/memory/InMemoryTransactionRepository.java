package bank.infrastructure.memory;

import bank.domain.Transaction;
import bank.service.port.TransactionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("memory")
public class InMemoryTransactionRepository implements TransactionRepository {
    private final Map<UUID, List<Transaction>> transactions = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        transactions.computeIfAbsent(transaction.accountId(), ignored -> new ArrayList<>()).add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        return transactions.getOrDefault(accountId, List.of()).stream()
            .sorted(Comparator.comparing((Transaction transaction) -> transaction.timestamp()).reversed())
                .toList();
    }
}
