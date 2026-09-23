package bank.infrastructure.memory;

import bank.domain.Transaction;
import bank.service.TransactionPage;
import bank.service.TransactionQuery;
import bank.service.port.TransactionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
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

    @Override
    public TransactionPage findPageByAccountId(UUID accountId, TransactionQuery query) {
        List<Transaction> ordered = findByAccountId(accountId).stream()
                .filter(transaction -> query.type() == null || transaction.type() == query.type())
                .filter(transaction -> query.text().isBlank() || transaction.description().toLowerCase(Locale.ROOT).contains(query.text().toLowerCase(Locale.ROOT)))
                .filter(transaction -> query.from() == null || !transaction.timestamp().isBefore(query.from()))
                .filter(transaction -> query.to() == null || !transaction.timestamp().isAfter(query.to()))
                .toList();
        int start = Math.min(query.page() * query.size(), ordered.size());
        int end = Math.min(start + query.size(), ordered.size());
        return new TransactionPage(ordered.subList(start, end), query.page(), query.size(), ordered.size());
    }
}
