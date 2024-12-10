package com.example.customer.service;

import com.example.customer.exception.custom.*;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.repository.entity.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Customer> getAllCustomers() {
        try {
            return repository.findAll();
        } catch (Exception ex) {
            throw new DatabaseAccessException("Error accessing the database.");
        }
    }

    @Override
    public Optional<Customer> getCustomerById(UUID  uuid) {
        try {
            return repository.findById(uuid)
                    .or(() -> {
                        throw new CustomerNotFoundException("Customer with ID " + uuid + " not found");
                    });
        } catch (Exception ex) {
            throw new DatabaseAccessException("Error accessing the database.");
        }
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        validateCustomer(customer);

        if (repository.existsByFirstNameAndLastName(customer.getFirstName(), customer.getLastName())) {
            throw new DuplicateResourceException("Customer with the same name already exists");
        }

        if (repository.existsByEmailAddress(customer.getEmailAddress())) {
            throw new DuplicateResourceException("Customer with email " + customer.getEmailAddress() + " already exists");
        }

        try {
            return repository.save(customer);
        } catch (Exception ex) {
            throw new DatabaseAccessException("Error accessing the database.");
        }
    }

    @Override
    @Transactional
    public Customer updateCustomer(UUID uuid, Customer customer) {
        validateCustomer(customer);

        if (!repository.existsById(uuid)) {
            throw new CustomerNotFoundException("Customer with ID " + uuid + " not found");
        }

        // Check if the customer exists
        Customer existingCustomer = getCustomerById(uuid).get();

        // Update fields that can be changed
        existingCustomer.setFirstName(customer.getFirstName());
        existingCustomer.setMiddleName(customer.getMiddleName());
        existingCustomer.setLastName(customer.getLastName());
        existingCustomer.setEmailAddress(customer.getEmailAddress());
        existingCustomer.setPhoneNumber(customer.getPhoneNumber());

        try {
            return repository.save(existingCustomer);
        } catch (Exception ex) {
            throw new DatabaseAccessException("Error accessing the database.");
        }
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID uuid) {

        if (!repository.existsById(uuid)) {
            throw new CustomerNotFoundException("Customer with ID " + uuid + " not found");
        }

        try {
            repository.deleteById(uuid);
        } catch (Exception ex) {
            throw new DatabaseAccessException("Error accessing the database.");
        }
    }

    // Helper Methods

    private UUID parseUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new InvalidIdFormatException("Invalid ID format: " + id);
        }
    }

    private void validateCustomer(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isBlank()) {
            throw new ValidationException("Last name is required");
        }
        if (customer.getEmailAddress() == null || !customer.getEmailAddress().contains("@")) {
            throw new ValidationException("Invalid email address");
        }
    }
}
