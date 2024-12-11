package com.example.customer.service;

import com.example.customer.exception.custom.*;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.repository.entity.Customer;
import datadog.trace.api.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    @Trace
    public List<Customer> getAllCustomers() {
        try {
            List<Customer> customers = repository.findAll();
            logger.info("Successfully fetched {} customers", customers.size());
            return customers;
        } catch (Exception ex) {
            logger.error("Error while fetching all customers", ex);
            throw new DatabaseAccessException("Error accessing the database.");
        }
    }

    @Override
    @Trace
    public Optional<Customer> getCustomerById(UUID uuid) {
        try {
            Optional<Customer> customer = repository.findById(uuid);
            if (customer.isPresent()) {
                logger.info("Customer with ID {} found", uuid);
            } else {
                logger.warn("Customer with ID {} not found", uuid);
            }
            return customer.or(() -> {
                throw new CustomerNotFoundException("Customer with ID " + uuid + " not found");
            });
        } catch (Exception ex) {
            logger.error("Error while fetching customer with ID: {}", uuid, ex);
            throw new DatabaseAccessException("Error accessing the database.");
        }
    }

    @Override
    @Transactional
    @Trace
    public Customer saveCustomer(Customer customer) {
        validateCustomer(customer);

        if (repository.existsByFirstNameAndLastName(customer.getFirstName(), customer.getLastName())) {
            logger.warn("Duplicate customer with name: {} {}", customer.getFirstName(), customer.getLastName());
            throw new DuplicateResourceException("Customer with the same name already exists");
        }
        else if (repository.existsByEmailAddress(customer.getEmailAddress())) {
            logger.warn("Duplicate customer with email: {}", customer.getEmailAddress());
            throw new DuplicateResourceException("Customer with email " + customer.getEmailAddress() + " already exists");
        }
        else {
            try {
                Customer savedCustomer = repository.save(customer);
                logger.info("Customer saved successfully with ID: {}", savedCustomer.getId());
                return savedCustomer;
            } catch (Exception ex) {
                logger.error("Error while saving customer: {}", customer, ex);
                throw new DatabaseAccessException("Error accessing the database.");
            }
        }

    }

    @Override
    @Transactional
    @Trace
    public Customer updateCustomer(UUID uuid, Customer customer) {
        validateCustomer(customer);

        if (!repository.existsById(uuid)) {
            logger.warn("Customer with ID {} not found for update", uuid);
            throw new CustomerNotFoundException("Customer with ID " + uuid + " not found");
        }
        else {
            try {
                // Check if the customer exists
                Customer existingCustomer = getCustomerById(uuid).get();
                logger.info("Updating fields for customer with ID: {}", uuid);

                // Update fields that can be changed
                existingCustomer.setFirstName(customer.getFirstName());
                existingCustomer.setMiddleName(customer.getMiddleName());
                existingCustomer.setLastName(customer.getLastName());
                existingCustomer.setEmailAddress(customer.getEmailAddress());
                existingCustomer.setPhoneNumber(customer.getPhoneNumber());

                Customer updatedCustomer = repository.save(existingCustomer);
                logger.info("Customer with ID {} updated successfully", uuid);
                return updatedCustomer;
            } catch (Exception ex) {
                logger.error("Error while updating customer with ID: {}", uuid, ex);
                throw new DatabaseAccessException("Error accessing the database.");
            }
        }


    }

    @Override
    @Transactional
    @Trace
    public void deleteCustomer(UUID uuid) {

        if (!repository.existsById(uuid)) {
            logger.warn("Customer with ID {} not found for deletion", uuid);
            throw new CustomerNotFoundException("Customer with ID " + uuid + " not found");
        }

        try {
            repository.deleteById(uuid);
            logger.info("Customer with ID {} deleted successfully", uuid);
        } catch (Exception ex) {
            logger.error("Error while deleting customer with ID: {}", uuid, ex);
            throw new DatabaseAccessException("Error accessing the database.");
        }
    }

    // Helper Methods

    private UUID parseUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            logger.error("Invalid UUID format for string: {}", id, ex);
            throw new InvalidIdFormatException("Invalid ID format: " + id);
        }
    }

    private void validateCustomer(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().isBlank()) {
            logger.error("Validation failed: First name is required");
            throw new ValidationException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isBlank()) {
            logger.error("Validation failed: Last name is required");
            throw new ValidationException("Last name is required");
        }
        if (customer.getEmailAddress() == null || !customer.getEmailAddress().contains("@")) {
            logger.error("Validation failed: Invalid email address");
            throw new ValidationException("Invalid email address");
        }
        logger.info("Customer validation passed");
    }
}
