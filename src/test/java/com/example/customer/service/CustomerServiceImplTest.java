package com.example.customer.service;

import com.example.customer.exception.custom.*;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.repository.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample customer
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
        when(repository.findAll()).thenReturn(List.of(customer));

        // Act
        List<Customer> customers = service.getAllCustomers();

        // Assert
        assertNotNull(customers);
        assertEquals(1, customers.size());
        assertEquals(customer, customers.get(0));
        verify(repository, times(1)).findAll();
    }

    @Test
    void getAllCustomers_ShouldThrowDatabaseAccessException_WhenRepositoryFails() {
        // Arrange
        when(repository.findAll()).thenThrow(RuntimeException.class);

        // Act & Assert
        assertThrows(DatabaseAccessException.class, () -> service.getAllCustomers());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() {
        // Arrange
        UUID id = customer.getId();
        when(repository.findById(id)).thenReturn(Optional.of(customer));

        // Act
        Optional<Customer> result = service.getCustomerById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void saveCustomer_ShouldSaveCustomer_WhenValidCustomer() {
        // Arrange
        when(repository.existsByFirstNameAndLastName(customer.getFirstName(), customer.getLastName())).thenReturn(false);
        when(repository.existsByEmailAddress(customer.getEmailAddress())).thenReturn(false);
        when(repository.save(customer)).thenReturn(customer);

        // Act
        Customer savedCustomer = service.saveCustomer(customer);

        // Assert
        assertNotNull(savedCustomer);
        assertEquals(customer, savedCustomer);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void saveCustomer_ShouldThrowDuplicateResourceException_WhenDuplicateNameExists() {
        // Arrange
        when(repository.existsByFirstNameAndLastName(customer.getFirstName(), customer.getLastName())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> service.saveCustomer(customer));
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_ShouldUpdateCustomer_WhenValid() {
        // Arrange
        UUID id = customer.getId();
        Customer updatedCustomer = new Customer();
        updatedCustomer.setFirstName("Jane");
        updatedCustomer.setLastName("Smith");
        updatedCustomer.setEmailAddress("jane.smith@example.com");
        updatedCustomer.setPhoneNumber("9876543210");

        when(repository.existsById(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // Act
        Customer result = service.updateCustomer(id, updatedCustomer);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_ShouldDeleteCustomer_WhenCustomerExists() {
        // Arrange
        UUID id = customer.getId();
        when(repository.existsById(id)).thenReturn(true);

        // Act
        service.deleteCustomer(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void deleteCustomer_ShouldThrowCustomerNotFoundException_WhenCustomerDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> service.deleteCustomer(id));
        verify(repository, never()).deleteById(any());
    }
}

