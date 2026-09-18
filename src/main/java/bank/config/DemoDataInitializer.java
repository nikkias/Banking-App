package bank.config;

import bank.domain.Account;
import bank.domain.Customer;
import bank.service.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
@Profile("memory & !test")
public class DemoDataInitializer {
    @Bean
    CommandLineRunner demoData(BankService bankService) {
        return args -> {
            if (!bankService.getAccounts().isEmpty()) {
                return;
            }

            Customer anna = bankService.registerCustomer("employee", "Anna", "Muster", "customer");
            Customer maxim = bankService.registerCustomer("employee", "Maxim", "Schneider", "customer");
            Customer clara = bankService.registerCustomer("employee", "Clara", "Berger", "employee");

            Account giro = bankService.openAccount("employee", anna.id(), new BigDecimal("2450.00"));
            Account savings = bankService.openAccount("employee", maxim.id(), new BigDecimal("8200.00"));
            Account operations = bankService.openAccount("employee", clara.id(), new BigDecimal("15120.00"));

            bankService.deposit("employee", giro.id(), new BigDecimal("950.00"), "Gehaltseingang");
            bankService.withdraw("employee", giro.id(), new BigDecimal("120.00"), "Kartenzahlung Mobilität");
            bankService.transfer("employee", operations.id(), savings.id(), new BigDecimal("750.00"), "Umbuchung Rücklage");
            bankService.withdraw("employee", operations.id(), new BigDecimal("430.00"), "Versicherungsbeitrag");
        };
    }
}
