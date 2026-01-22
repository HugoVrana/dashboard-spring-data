package com.dashboard.controller.customers;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.model.entities.Customer;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Get Customer By ID")
@DisplayName("GET /customers/{id}")
public class GetCustomerByIdTest extends BaseCustomersControllerTest {
    @Test
    @DisplayName("should return customer when found")
    void getCustomerById_ReturnsCustomerWhenFound() throws Exception {
        Customer testCustomer = createTestCustomer();
        CustomerRead testCustomerRead = createTestCustomerRead();

        when(customersService.getCustomer(testCustomerId)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toRead(testCustomer)).thenReturn(testCustomerRead);

        mockMvc.perform(get("/customers/{id}", testCustomerId.toHexString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testCustomerId.toHexString()))
                .andExpect(jsonPath("$.name").value(testCustomerName))
                .andExpect(jsonPath("$.email").value(testCustomerEmail));
    }

    @Test
    @DisplayName("should return 404 when customer not found")
    void getCustomerById_Returns404WhenNotFound() throws Exception {
        when(customersService.getCustomer(testCustomerId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customers/{id}", testCustomerId.toHexString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 404 when id is invalid")
    void getCustomerById_Returns404WhenIdInvalid() throws Exception {
        mockMvc.perform(get("/customers/{id}", "invalid-id"))
                .andExpect(status().isNotFound());
    }
}
