package bank.service;

import bank.domain.Account;
import bank.domain.AuditEvent;
import bank.domain.Customer;
import bank.domain.Transaction;
import bank.domain.TransactionType;
import bank.service.port.AccountRepository;
import bank.service.port.AuditEventRepository;
import bank.service.port.CustomerRepository;
import bank.service.port.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public final class BankService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditEventRepository auditEventRepository;
    private final Clock clock;

    public BankService(
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            AuditEventRepository auditEventRepository,
            Clock clock) {
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository");
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository");
        this.transactionRepository = Objects.requireNonNull(transactionRepository, "transactionRepository");
        this.auditEventRepository = Objects.requireNonNull(auditEventRepository, "auditEventRepository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public synchronized Customer registerCustomer(String firstName, String lastName) {
        return registerCustomer("system", firstName, lastName);
    }

    public synchronized Customer registerCustomer(String actor, String firstName, String lastName) {
        return registerCustomer(actor, firstName, lastName, actor);
    }

    public synchronized Customer registerCustomer(String actor, String firstName, String lastName, String ownerUsername) {
        Customer customer = new Customer(UUID.randomUUID(), firstName, lastName, normalizeOwner(ownerUsername, actor));
        Customer saved = customerRepository.save(customer);
        audit(actor, "CUSTOMER_REGISTERED", "CUSTOMER", saved.id(), null, "Customer registered");
        return saved;
    }

    public synchronized Account openAccount(UUID customerId, BigDecimal openingBalance) {
        return openAccount("system", customerId, openingBalance);
    }

    public synchronized Account openAccount(String actor, UUID customerId, BigDecimal openingBalance) {
        requireCustomer(customerId);
        Account account = new Account(UUID.randomUUID(), createIban(), customerId, openingBalance);
        accountRepository.save(account);
        if (openingBalance.signum() > 0) {
            record(account, TransactionType.DEPOSIT, openingBalance, "Opening balance");
        }
        audit(actor, "ACCOUNT_OPENED", "ACCOUNT", account.id(), openingBalance, "Account opened");
        return account;
    }

    public synchronized void deposit(UUID accountId, BigDecimal amount, String description) {
        deposit("system", accountId, amount, description);
    }

    public synchronized void deposit(String actor, UUID accountId, BigDecimal amount, String description) {
        Account account = requireAccount(accountId);
        account.deposit(amount);
        accountRepository.save(account);
        record(account, TransactionType.DEPOSIT, amount, description);
        audit(actor, "DEPOSIT", "ACCOUNT", account.id(), amount, description);
    }

    public synchronized void withdraw(UUID accountId, BigDecimal amount, String description) {
        withdraw("system", accountId, amount, description);
    }

    public synchronized void withdraw(String actor, UUID accountId, BigDecimal amount, String description) {
        Account account = requireAccount(accountId);
        account.withdraw(amount);
        accountRepository.save(account);
        record(account, TransactionType.WITHDRAWAL, amount, description);
        audit(actor, "WITHDRAWAL", "ACCOUNT", account.id(), amount, description);
    }

    public synchronized void transfer(UUID sourceId, UUID targetId, BigDecimal amount, String description) {
        transfer("system", sourceId, targetId, amount, description);
    }

    public synchronized void transfer(String actor, UUID sourceId, UUID targetId, BigDecimal amount, String description) {
        if (sourceId.equals(targetId)) {
            throw new IllegalArgumentException("Source and target account must differ");
        }
        Account source = requireAccount(sourceId);
        Account target = requireAccount(targetId);
        source.withdraw(amount);
        target.deposit(amount);
        accountRepository.save(source);
        accountRepository.save(target);
        record(source, TransactionType.TRANSFER_OUT, amount, description);
        record(target, TransactionType.TRANSFER_IN, amount, description);
        audit(actor, "TRANSFER_OUT", "ACCOUNT", source.id(), amount, description);
        audit(actor, "TRANSFER_IN", "ACCOUNT", target.id(), amount, description);
    }

    public synchronized Account getAccount(UUID accountId) {
        return requireAccount(accountId);
    }

    public synchronized List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public synchronized List<Account> getAccountsForOwner(String ownerUsername) {
        return customerRepository.findByOwnerUsername(Objects.requireNonNull(ownerUsername, "ownerUsername")).stream()
                .flatMap(customer -> accountRepository.findAll().stream()
                        .filter(account -> account.customerId().equals(customer.id())))
                .toList();
    }

    public synchronized boolean isAccountOwnedBy(UUID accountId, String ownerUsername) {
        Account account = requireAccount(accountId);
        return customerRepository.findById(account.customerId())
                .map(customer -> customer.ownerUsername().equals(ownerUsername))
                .orElse(false);
    }

    public synchronized List<Transaction> getTransactions(UUID accountId) {
        requireAccount(accountId);
        return transactionRepository.findByAccountId(accountId);
    }

    public synchronized List<AuditEvent> getAuditEvents() {
        return auditEventRepository.findAll();
    }

    private void record(Account account, TransactionType type, BigDecimal amount, String description) {
        transactionRepository.save(new Transaction(
                UUID.randomUUID(), account.id(), type, amount, Instant.now(clock), description));
    }

    private void audit(String actor, String action, String resourceType, UUID resourceId, BigDecimal amount, String description) {
        auditEventRepository.save(new AuditEvent(
                UUID.randomUUID(), actor, action, resourceType, resourceId, amount, Instant.now(clock), "SUCCESS", description));
    }

    private void requireCustomer(UUID customerId) {
        Objects.requireNonNull(customerId, "customerId");
        if (customerRepository.findById(customerId).isEmpty()) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
    }

    private Account requireAccount(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId");
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
    }

    private String createIban() {
        return "DE" + String.format("%018d", accountRepository.count() + 1);
    }

    private String normalizeOwner(String ownerUsername, String fallbackActor) {
        if (ownerUsername == null || ownerUsername.isBlank()) {
            return fallbackActor == null || fallbackActor.isBlank() ? "system" : fallbackActor;
        }
        return ownerUsername.trim();
    }
}
