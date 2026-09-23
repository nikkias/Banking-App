package bank.service;

import bank.domain.Account;
import bank.domain.Customer;
import bank.domain.TransactionType;
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
    void appliesTheDailyDebitLimitAcrossWithdrawalsAndTransfers() {
        BankService limitedBankService = new BankService(
                new InMemoryCustomerRepository(),
                new InMemoryAccountRepository(),
                new InMemoryTransactionRepository(),
                new InMemoryAuditEventRepository(),
                Clock.systemUTC(),
                new BigDecimal("100.00"));
        Customer sourceCustomer = limitedBankService.registerCustomer("Anna", "Muster");
        Customer targetCustomer = limitedBankService.registerCustomer("Max", "Beispiel");
        Account source = limitedBankService.openAccount(sourceCustomer.id(), new BigDecimal("200.00"));
        Account target = limitedBankService.openAccount(targetCustomer.id(), BigDecimal.ZERO);

        limitedBankService.withdraw(source.id(), new BigDecimal("60.00"), "ATM");

        assertThatThrownBy(() -> limitedBankService.transfer(source.id(), target.id(), new BigDecimal("50.00"), "Rent"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Daily debit limit exceeded");
        assertThat(source.balance()).isEqualByComparingTo("140.00");
        assertThat(target.balance()).isEqualByComparingTo("0.00");
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

    @Test
    void recordsTransactionExportsForComplianceAudit() {
        Account account = openAccount("Anna", "Muster", "100.00");

        bankService.recordTransactionExport("anna", account.id(), "csv", "type=DEPOSIT; from=2026-09-01");

        assertThat(bankService.getAuditEvents())
                .anySatisfy(event -> {
                    assertThat(event.actor()).isEqualTo("anna");
                    assertThat(event.action()).isEqualTo("TRANSACTION_EXPORT");
                    assertThat(event.resourceId()).isEqualTo(account.id());
                    assertThat(event.description()).contains("format=CSV", "type=DEPOSIT");
                });
    }

    @Test
    void returnsBoundedTransactionHistoryPages() {
        Account account = openAccount("Anna", "Muster", "100.00");
        bankService.deposit(account.id(), new BigDecimal("20.00"), "Salary");
        bankService.withdraw(account.id(), new BigDecimal("10.00"), "Card");

        TransactionPage firstPage = bankService.getTransactionPage(account.id(), 0, 2);
        TransactionPage secondPage = bankService.getTransactionPage(account.id(), 1, 2);

        assertThat(firstPage.content()).hasSize(2);
        assertThat(firstPage.totalElements()).isEqualTo(3);
        assertThat(secondPage.content()).hasSize(1);
        assertThat(secondPage.totalElements()).isEqualTo(3);
    }

    @Test
    void filtersTransactionPagesByTypeAndText() {
        Account account = openAccount("Anna", "Muster", "100.00");
        bankService.deposit(account.id(), new BigDecimal("20.00"), "Monthly salary");
        bankService.withdraw(account.id(), new BigDecimal("10.00"), "Card payment");

        TransactionPage page = bankService.getTransactionPage(account.id(),
                new TransactionQuery(TransactionType.DEPOSIT, "salary", null, null, 0, 25));

        assertThat(page.totalElements()).isEqualTo(1);
        assertThat(page.content()).singleElement().satisfies(transaction -> {
            assertThat(transaction.type()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(transaction.description()).isEqualTo("Monthly salary");
        });
    }

    private Account openAccount(String firstName, String lastName, String openingBalance) {
        Customer customer = bankService.registerCustomer(firstName, lastName);
        return bankService.openAccount(customer.id(), new BigDecimal(openingBalance));
    }
}