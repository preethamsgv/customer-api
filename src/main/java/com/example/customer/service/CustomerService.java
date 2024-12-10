package com.example.customer.service;

import com.example.customer.repository.entity.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(UUID id);
    Customer saveCustomer(Customer customer);
    Customer updateCustomer(UUID id, Customer customer);
    void deleteCustomer(UUID id);
}
