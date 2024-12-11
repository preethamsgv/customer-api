package com.example.customer.integration;

import com.example.customer.repository.entity.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer testCustomer;

    @BeforeEach
    void setup() throws Exception {
        // Create a customer to use for GET, UPDATE, and DELETE tests
        testCustomer = new Customer();
        testCustomer.setFirstName("Shiva");
        testCustomer.setLastName("Kumar");
        testCustomer.setEmailAddress("Shiva.Kumar@example.com");
        testCustomer.setPhoneNumber("3218762345");

        String customerJson = objectMapper.writeValueAsString(testCustomer);
        String response = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Parse the created customer to get the ID
        Customer createdCustomer = objectMapper.readValue(response, Customer.class);
        testCustomer.setId(createdCustomer.getId());
    }

    @Test
    @Transactional
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() throws Exception {
        mockMvc.perform(get("/api/customers/" + testCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Shiva"))
                .andExpect(jsonPath("$.lastName").value("Kumar"))
                .andExpect(jsonPath("$.emailAddress").value("Shiva.Kumar@example.com"));
    }

    @Test
    @Transactional
    void getCustomerById_ShouldReturnNotFound_WhenCustomerKumarsNotExist() throws Exception {
        mockMvc.perform(get("/api/customers/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateCustomer_ShouldReturnUpdatedCustomer_WhenCustomerExists() throws Exception {
        testCustomer.setFirstName("Lalita");
        testCustomer.setEmailAddress("Lalita.Kumar@example.com");

        String updatedCustomerJson = objectMapper.writeValueAsString(testCustomer);

        mockMvc.perform(put("/api/customers/" + testCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCustomerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Lalita"))
                .andExpect(jsonPath("$.emailAddress").value("Lalita.Kumar@example.com"));
    }

    @Test
    @Transactional
    void updateCustomer_ShouldReturnNotFound_WhenCustomerKumarsNotExist() throws Exception {
        Customer nonExistentCustomer = new Customer();
        nonExistentCustomer.setFirstName("Dummy");
        nonExistentCustomer.setLastName("User");
        nonExistentCustomer.setEmailAddress("Dummy.user@example.com");

        String nonExistentCustomerJson = objectMapper.writeValueAsString(nonExistentCustomer);

        mockMvc.perform(put("/api/customers/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nonExistentCustomerJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteCustomer_ShouldReturnNoContent_WhenCustomerExists() throws Exception {
        mockMvc.perform(delete("/api/customers/" + testCustomer.getId()))
                .andExpect(status().isNoContent());

        // Verify the customer is deleted
        mockMvc.perform(get("/api/customers/" + testCustomer.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteCustomer_ShouldReturnNotFound_WhenCustomerKumarsNotExist() throws Exception {
        mockMvc.perform(delete("/api/customers/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
