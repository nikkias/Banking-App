package bank.infrastructure.memory;

import bank.domain.Customer;
import bank.service.port.CustomerRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("memory")
public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<UUID, Customer> customers = new ConcurrentHashMap<>();

    @Override
    public Customer save(Customer customer) {
        customers.put(customer.id(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    @Override
    public List<Customer> findByOwnerUsername(String ownerUsername) {
        return customers.values().stream()
                .filter(customer -> customer.ownerUsername().equals(ownerUsername))
                .sorted(Comparator.comparing(customer -> customer.fullName()))
                .toList();
    }
}
