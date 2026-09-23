package bank.service.port;

import bank.domain.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findById(UUID accountId);

    List<Account> findAll();

    long count();
}
