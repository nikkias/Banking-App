package bank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    OpenAPI bankPlatformOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank24 Financial Services API")
                        .version("0.1.0")
                        .description("Investor-ready Banking MVP API for accounts, transactions, roles and audit events.")
                        .contact(new Contact().name("Bank24 Platform Team"))
                        .license(new License().name("Demo project")));
    }
}