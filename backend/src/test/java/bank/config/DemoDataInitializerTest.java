package bank.config;

import bank.infrastructure.memory.InMemoryAccountRepository;
import bank.infrastructure.memory.InMemoryAuditEventRepository;
import bank.infrastructure.memory.InMemoryCustomerRepository;
import bank.infrastructure.memory.InMemoryTransactionRepository;
import bank.service.BankService;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

class DemoDataInitializerTest {
    @Test
    void createsIsolatedCustomerPortfoliosOnlyOnce() throws Exception {
        BankService bankService = new BankService(
                new InMemoryCustomerRepository(),
                new InMemoryAccountRepository(),
                new InMemoryTransactionRepository(),
                new InMemoryAuditEventRepository(),
                Clock.systemUTC());

        DemoDataInitializer initializer = new DemoDataInitializer();
        initializer.demoData(bankService).run();
        initializer.demoData(bankService).run();

        assertThat(bankService.getAccounts()).hasSize(3);
        assertThat(bankService.getAccountsForOwner("anna")).hasSize(1);
        assertThat(bankService.getAccountsForOwner("maxim")).hasSize(1);
        assertThat(bankService.getAccountsForOwner("clara")).hasSize(1);
        assertThat(bankService.getAccountsForOwner("customer")).isEmpty();
    }
}