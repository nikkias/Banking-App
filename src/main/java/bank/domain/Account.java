package bank.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class Account {
    private final UUID id;
    private final String iban;
    private final UUID customerId;
    private BigDecimal balance;

    public Account(UUID id, String iban, UUID customerId, BigDecimal openingBalance) {
        this.id = Objects.requireNonNull(id, "id");
        this.iban = Objects.requireNonNull(iban, "iban");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.balance = requireNonNegative(openingBalance, "openingBalance");
    }

    public UUID id() {
        return id;
    }

    public String iban() {
        return iban;
    }

    public UUID customerId() {
        return customerId;
    }

    public BigDecimal balance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(requirePositive(amount));
    }

    public void withdraw(BigDecimal amount) {
        BigDecimal withdrawal = requirePositive(amount);
        if (balance.compareTo(withdrawal) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance = balance.subtract(withdrawal);
    }

    private static BigDecimal requirePositive(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        return amount;
    }

    private static BigDecimal requireNonNegative(BigDecimal amount, String fieldName) {
        Objects.requireNonNull(amount, fieldName);
        if (amount.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }
        return amount;
    }
}
