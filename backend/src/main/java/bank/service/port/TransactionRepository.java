package bank.service.port;

import bank.domain.Transaction;
import bank.service.TransactionPage;
import bank.service.TransactionQuery;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findByAccountId(UUID accountId);

    TransactionPage findPageByAccountId(UUID accountId, TransactionQuery query);
}
