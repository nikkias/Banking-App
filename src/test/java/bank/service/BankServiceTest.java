package bank.service;

import bank.domain.Account;
import bank.domain.Customer;
import bank.infrastructure.memory.InMemoryAccountRepository;
import bank.infrastructure.memory.InMemoryAuditEventRepository;
import bank.infrastructure.memory.InMemoryCustomerRepository;
import bank.infrastructure.memory.InMemoryTransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankServiceTest {
    private final BankService bankService = new BankService(
            new InMemoryCustomerRepository(),
            new InMemoryAccountRepository(),
            new InMemoryTransactionRepository(),
            new InMemoryAuditEventRepository(),
            Clock.systemUTC());

    @Test
    void opensAccountForRegisteredCustomer() {
        Customer customer = bankService.registerCustomer("Anna", "Muster");

        Account account = bankService.openAccount(customer.id(), new BigDecimal("100.00"));

        assertThat(account.customerId()).isEqualTo(customer.id());
        assertThat(account.iban()).startsWith("DE");
        assertThat(account.balance()).isEqualByComparingTo("100.00");
        assertThat(bankService.getTransactions(account.id())).hasSize(1);
        assertThat(bankService.getAuditEvents()).hasSize(2);
    }

    @Test
    void transfersMoneyBetweenAccounts() {
        Account source = openAccount("Anna", "Muster", "100.00");
        Account target = openAccount("Max", "Beispiel", "10.00");

        bankService.transfer(source.id(), target.id(), new BigDecimal("30.00"), "Miete");

        assertThat(source.balance()).isEqualByComparingTo("70.00");
        assertThat(target.balance()).isEqualByComparingTo("40.00");
        assertThat(bankService.getTransactions(source.id())).hasSize(2);
        assertThat(bankService.getTransactions(target.id())).hasSize(2);
        assertThat(bankService.getAuditEvents()).extracting(event -> event.action())
            .contains("TRANSFER_OUT", "TRANSFER_IN");
    }

    @Test
    void rejectsWithdrawalWhenBalanceIsTooLow() {
        Account account = openAccount("Anna", "Muster", "20.00");

        assertThatThrownBy(() -> bankService.withdraw(account.id(), new BigDecimal("25.00"), "ATM"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient funds");

        assertThat(account.balance()).isEqualByComparingTo("20.00");
    }

    @Test
    void filtersAccountsByOwnerUsername() {
        Customer customer = bankService.registerCustomer("employee", "Clara", "Kunde", "customer");
        Customer otherCustomer = bankService.registerCustomer("employee", "Ben", "Fremd", "other");
        Account ownedAccount = bankService.openAccount("employee", customer.id(), new BigDecimal("70.00"));
        bankService.openAccount("employee", otherCustomer.id(), new BigDecimal("20.00"));

        assertThat(bankService.getAccountsForOwner("customer"))
            .extracting(account -> account.id())
                .containsExactly(ownedAccount.id());
        assertThat(bankService.isAccountOwnedBy(ownedAccount.id(), "customer")).isTrue();
        assertThat(bankService.isAccountOwnedBy(ownedAccount.id(), "other")).isFalse();
    }

    private Account openAccount(String firstName, String lastName, String openingBalance) {
        Customer customer = bankService.registerCustomer(firstName, lastName);
        return bankService.openAccount(customer.id(), new BigDecimal(openingBalance));
    }
}