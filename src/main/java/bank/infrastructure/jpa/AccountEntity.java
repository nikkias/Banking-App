package bank.infrastructure.jpa;

import bank.domain.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
class AccountEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String iban;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    private long version;

    protected AccountEntity() {
    }

    private AccountEntity(UUID id, String iban, UUID customerId, BigDecimal balance) {
        this.id = id;
        this.iban = iban;
        this.customerId = customerId;
        this.balance = balance;
    }

    static AccountEntity fromDomain(Account account) {
        return new AccountEntity(account.id(), account.iban(), account.customerId(), account.balance());
    }

    Account toDomain() {
        return new Account(id, iban, customerId, balance);
    }
}
