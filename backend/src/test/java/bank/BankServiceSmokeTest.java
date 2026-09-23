package bank;

import bank.domain.Account;
import bank.domain.Customer;
import bank.infrastructure.memory.InMemoryAuditEventRepository;
import bank.infrastructure.memory.InMemoryAccountRepository;
import bank.infrastructure.memory.InMemoryCustomerRepository;
import bank.infrastructure.memory.InMemoryTransactionRepository;
import bank.service.BankService;

import java.math.BigDecimal;
import java.time.Clock;

public final class BankServiceSmokeTest {
    public static void main(String[] args) {
        BankService bank = new BankService(
                new InMemoryCustomerRepository(),
                new InMemoryAccountRepository(),
                new InMemoryTransactionRepository(),
                new InMemoryAuditEventRepository(),
                Clock.systemUTC());
        Customer sourceCustomer = bank.registerCustomer("Anna", "Muster");
        Customer targetCustomer = bank.registerCustomer("Max", "Beispiel");
        Account source = bank.openAccount(sourceCustomer.id(), new BigDecimal("100.00"));
        Account target = bank.openAccount(targetCustomer.id(), BigDecimal.ZERO);

        bank.transfer(source.id(), target.id(), new BigDecimal("40.00"), "Interne Überweisung");

        assertMoney("60.00", source.balance());
        assertMoney("40.00", target.balance());
        if (bank.getTransactions(source.id()).size() != 2) {
            throw new AssertionError("Source account should have opening and transfer booking");
        }
        if (bank.getTransactions(target.id()).size() != 1) {
            throw new AssertionError("Target account should have one transfer booking");
        }
        System.out.println("BankService smoke test passed");
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        if (new BigDecimal(expected).compareTo(actual) != 0) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}