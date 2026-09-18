package bank.service.port;

import bank.domain.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findByAccountId(UUID accountId);
}
