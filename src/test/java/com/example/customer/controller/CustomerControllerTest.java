package com.example.customer.controller;

import com.example.customer.exception.custom.InvalidIdFormatException;
import com.example.customer.repository.entity.Customer;
import com.example.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("John");
        customer.setMiddleName("M");
        customer.setLastName("Doe");
        customer.setEmailAddress("john.doe@example.com");
        customer.setPhoneNumber("1234567890");
    }

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers() {
        // Arrange
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(customer));

        // Act
        var customers = customerController.getAllCustomers();

        // Assert
        assertNotNull(customers);
        assertEquals(1, customers.size());
        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() {
        // Arrange
        UUID id = customer.getId();
        when(customerService.getCustomerById(id)).thenReturn(Optional.of(customer));

        // Act
        ResponseEntity<Customer> response = customerController.getCustomerById(id.toString());

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
        verify(customerService, times(1)).getCustomerById(id);
    }

    @Test
    void getCustomerById_ShouldReturnNotFound_WhenCustomerDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(customerService.getCustomerById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Customer> response = customerController.getCustomerById(id.toString());

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customerService, times(1)).getCustomerById(id);
    }

    @Test
    void getCustomerById_ShouldThrowInvalidIdFormatException_WhenIdIsInvalid() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerController.getCustomerById("invalid-uuid");
        });

        // Verify that the service is never called
        verify(customerService, never()).getCustomerById(any());
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() {
        // Arrange
        when(customerService.saveCustomer(customer)).thenReturn(customer);

        // Act
        ResponseEntity<Customer> response = customerController.createCustomer(customer);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(customer, response.getBody());
        verify(customerService, times(1)).saveCustomer(customer);
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer() {
        // Arrange
        UUID id = customer.getId();
        when(customerService.updateCustomer(eq(id), any(Customer.class))).thenReturn(customer);

        // Act
        ResponseEntity<Customer> response = customerController.updateCustomer(id.toString(), customer);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
        verify(customerService, times(1)).updateCustomer(eq(id), any(Customer.class));
    }

    @Test
    void updateCustomer_ShouldThrowInvalidIdFormatException_WhenIdIsInvalid() {
        // Act & Assert
        assertThrows(InvalidIdFormatException.class, () -> {
            customerController.updateCustomer("invalid-uuid", customer);
        });
    }

    @Test
    void deleteCustomer_ShouldReturnNoContent_WhenCustomerExists() {
        // Arrange
        UUID id = customer.getId();
        when(customerService.getCustomerById(id)).thenReturn(Optional.of(customer));
        doNothing().when(customerService).deleteCustomer(id);

        // Act
        ResponseEntity<Void> response = customerController.deleteCustomer(id.toString());

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerService, times(1)).deleteCustomer(id);
    }

    @Test
    void deleteCustomer_ShouldReturnNotFound_WhenCustomerDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(customerService.getCustomerById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Void> response = customerController.deleteCustomer(id.toString());

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customerService, times(0)).deleteCustomer(id);
    }
}
