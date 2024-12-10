package com.example.customer.controller;


import com.example.customer.exception.custom.CustomerNotFoundException;
import com.example.customer.exception.custom.InvalidIdFormatException;
import com.example.customer.repository.entity.Customer;
import com.example.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return service.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        Optional<Customer> customer = service.getCustomerById(uuid);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = service.saveCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String id,
            @RequestBody Customer customerUpdateRequest) {
        try {
            // Parse the ID to validate it as UUID
            UUID uuid = UUID.fromString(id);

            // Save the updated customer
            Customer updatedCustomer = service.updateCustomer(uuid, customerUpdateRequest);
            return ResponseEntity.ok(updatedCustomer);

        } catch (IllegalArgumentException ex) {
            throw new InvalidIdFormatException("Invalid ID format: " + id);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        if (service.getCustomerById(uuid).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteCustomer(uuid);
        return ResponseEntity.noContent().build();
    }
}
