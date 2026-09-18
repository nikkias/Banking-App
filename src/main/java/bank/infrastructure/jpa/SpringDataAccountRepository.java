package bank.infrastructure.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Profile("postgres")
interface SpringDataAccountRepository extends JpaRepository<AccountEntity, UUID> {
}
