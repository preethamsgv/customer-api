package com.example.customer.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String firstName;

    private String middleName;

    @Column(nullable = false, unique = true)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    private String phoneNumber;
}

