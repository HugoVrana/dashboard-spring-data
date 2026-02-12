package com.dashboard.integration.customers;

import com.dashboard.integration.BaseIntegrationTest;
import com.dashboard.model.entities.Customer;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E integration tests for Customer endpoints.
 */
@Feature("Customer E2E")
@DisplayName("Customer E2E Tests")
public class CustomerE2ETest extends BaseIntegrationTest {

    @Test
    @Story("Get All Customers")
    @DisplayName("GET /customers/ returns all active customers")
    void getAllCustomers_ReturnsAllActiveCustomers() throws Exception {
        Customer customer1 = createAndSaveCustomer();
        Customer customer2 = createAndSaveCustomer();

        mockMvc.perform(get("/customers/")
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        customer1.get_id().toHexString(),
                        customer2.get_id().toHexString()
                )));
    }

    @Test
    @Story("Get Customer By ID")
    @DisplayName("GET /customers/{id} retrieves by ID")
    void getCustomerById_RetrievesById() throws Exception {
        Customer customer = createAndSaveCustomer();

        mockMvc.perform(get("/customers/" + customer.get_id().toHexString())
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.get_id().toHexString()))
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.email").value(customer.getEmail()));
    }

    @Test
    @Story("Get Customer Count")
    @DisplayName("GET /customers/count returns correct count")
    void getCustomerCount_ReturnsCorrectCount() throws Exception {
        createAndSaveCustomer();
        createAndSaveCustomer();
        createAndSaveCustomer();

        mockMvc.perform(get("/customers/count")
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @Story("Soft Delete Exclusion")
    @DisplayName("GET /customers/ excludes soft-deleted customers")
    void getAllCustomers_ExcludesSoftDeleted() throws Exception {
        Customer activeCustomer = createAndSaveCustomer();

        // Create and soft-delete a customer
        Customer deletedCustomer = createAndSaveCustomer();
        deletedCustomer.setAudit(createDeletedAudit());
        customersRepository.save(deletedCustomer);

        mockMvc.perform(get("/customers/")
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(activeCustomer.get_id().toHexString()));
    }

    @Test
    @Story("Customer Not Found")
    @DisplayName("GET /customers/{id} returns 404 for non-existent customer")
    void getCustomerById_Returns404ForNonExistent() throws Exception {
        mockMvc.perform(get("/customers/507f1f77bcf86cd799439011")
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Story("Invalid ID")
    @DisplayName("GET /customers/{id} returns 404 for invalid ID format")
    void getCustomerById_Returns404ForInvalidId() throws Exception {
        mockMvc.perform(get("/customers/invalid-id")
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Story("Customer Count Excludes Deleted")
    @DisplayName("GET /customers/count excludes soft-deleted customers")
    void getCustomerCount_ExcludesSoftDeleted() throws Exception {
        createAndSaveCustomer();
        createAndSaveCustomer();

        // Create and soft-delete a customer
        Customer deletedCustomer = createAndSaveCustomer();
        deletedCustomer.setAudit(createDeletedAudit());
        customersRepository.save(deletedCustomer);

        mockMvc.perform(get("/customers/count")
                        .header("Authorization", authHeader("dashboard-customers-read")))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }
}
