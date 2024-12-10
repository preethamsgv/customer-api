package com.example.customer.repository;


import com.example.customer.repository.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByEmailAddress(String emailAddress);
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
}

