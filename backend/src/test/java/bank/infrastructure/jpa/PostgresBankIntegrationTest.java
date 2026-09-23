package bank.infrastructure.jpa;

import bank.domain.Account;
import bank.domain.Customer;
import bank.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("postgres")
@Testcontainers
class PostgresBankIntegrationTest {
    @Container
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bank_platform")
            .withUsername("bank")
            .withPassword("bank");

    @Autowired
    private BankService bankService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.execute("delete from audit_events");
        jdbcTemplate.execute("delete from transactions");
        jdbcTemplate.execute("delete from accounts");
        jdbcTemplate.execute("delete from customers");
    }

    @Test
    void persistsSuccessiveMoneyMovementsTransactionsAndAuditEvents() {
        Customer customer = bankService.registerCustomer("employee", "Ada", "Lovelace", "ada");
        Account account = bankService.openAccount("employee", customer.id(), new BigDecimal("100.00"));

        bankService.deposit("employee", account.id(), new BigDecimal("40.00"), "Salary credit");
        bankService.withdraw("employee", account.id(), new BigDecimal("25.00"), "Card payment");

        assertThat(bankService.getAccount(account.id()).balance()).isEqualByComparingTo("115.00");
        assertThat(bankService.getTransactions(account.id())).hasSize(3);
        assertThat(bankService.getAuditEvents()).hasSize(4);
        assertThat(jdbcTemplate.queryForObject("select count(*) from flyway_schema_history", Integer.class)).isGreaterThan(0);
    }
}