package bank.domain;

import java.util.Objects;
import java.util.UUID;

public record Customer(UUID id, String firstName, String lastName, String ownerUsername) {
    public Customer(UUID id, String firstName, String lastName) {
        this(id, firstName, lastName, "system");
    }

    public Customer {
        Objects.requireNonNull(id, "id");
        requireText(firstName, "firstName");
        requireText(lastName, "lastName");
        requireText(ownerUsername, "ownerUsername");
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}
