package bank.infrastructure.jpa;

import bank.domain.Customer;
import bank.service.port.CustomerRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.UUID;

@Repository
@Profile("postgres")
public class JpaCustomerRepositoryAdapter implements CustomerRepository {
    private final SpringDataCustomerRepository repository;

    public JpaCustomerRepositoryAdapter(SpringDataCustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = CustomerEntity.fromDomain(Objects.requireNonNull(customer, "customer"));
        return repository.save(Objects.requireNonNull(entity, "entity")).toDomain();
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return repository.findById(Objects.requireNonNull(customerId, "customerId")).map(entity -> entity.toDomain());
    }

    @Override
    public List<Customer> findByOwnerUsername(String ownerUsername) {
        return repository.findByOwnerUsernameOrderByLastNameAscFirstNameAsc(Objects.requireNonNull(ownerUsername, "ownerUsername"))
                .stream()
                .map(entity -> entity.toDomain())
                .toList();
    }
}
