package com.example.customer.controller;

import com.example.customer.exception.custom.CustomerNotFoundException;
import com.example.customer.exception.custom.InvalidIdFormatException;
import com.example.customer.repository.entity.Customer;
import com.example.customer.service.CustomerService;
import datadog.trace.api.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    @Trace
    public List<Customer> getAllCustomers() {
        logger.info("Fetching all customers");
        List<Customer> customers = service.getAllCustomers();
        return customers;
    }

    @GetMapping("/{id}")
    @Trace
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        logger.info("Fetching customer with ID: {}", id);

        UUID uuid = UUID.fromString(id);
        Optional<Customer> customer = service.getCustomerById(uuid);
        if (customer.isPresent()) {
            logger.info("Customer found: {}", customer.get());
            return ResponseEntity.ok(customer.get());
        } else {
            logger.warn("Customer with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    @Trace
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        logger.info("Creating a new customer: {}", customer);
        Customer savedCustomer = service.saveCustomer(customer);
        logger.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    @PutMapping("/{id}")
    @Trace
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String id,
            @RequestBody Customer customerUpdateRequest) {
        logger.info("Updating customer with ID: {}", id);

        UUID uuid = UUID.fromString(id);
        Customer updatedCustomer = service.updateCustomer(uuid, customerUpdateRequest);
        return ResponseEntity.ok(updatedCustomer);

    }

    @DeleteMapping("/{id}")
    @Trace
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        logger.info("Deleting customer with ID: {}", id);

        UUID uuid = UUID.fromString(id);
        Optional<Customer> customer = service.getCustomerById(uuid);

        if (customer.isPresent()) {
            service.deleteCustomer(uuid);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
