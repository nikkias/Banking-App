package bank.infrastructure.jpa;

import bank.domain.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "customers")
class CustomerEntity {
    @Id
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "owner_username", nullable = false)
    private String ownerUsername;

    protected CustomerEntity() {
    }

    private CustomerEntity(UUID id, String firstName, String lastName, String ownerUsername) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ownerUsername = ownerUsername;
    }

    static CustomerEntity fromDomain(Customer customer) {
        return new CustomerEntity(customer.id(), customer.firstName(), customer.lastName(), customer.ownerUsername());
    }

    Customer toDomain() {
        return new Customer(id, firstName, lastName, ownerUsername);
    }
}
