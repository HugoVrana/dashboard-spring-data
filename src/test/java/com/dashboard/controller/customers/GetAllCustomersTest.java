package com.dashboard.controller.customers;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.model.entities.Customer;
import io.qameta.allure.Story;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Story("Get All Customers")
@DisplayName("GET /customers")
public class GetAllCustomersTest extends BaseCustomersControllerTest {
    @Test
    @DisplayName("should return all customers")
    void getAllCustomers_ReturnsAllCustomers() throws Exception {
        Customer testCustomer = createTestCustomer();
        CustomerRead testCustomerRead = createTestCustomerRead();

        when(customersService.getAllCustomers()).thenReturn(List.of(testCustomer));
        when(customerMapper.toRead(testCustomer)).thenReturn(testCustomerRead);

        mockMvc.perform(get("/customers/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testCustomerId.toHexString()))
                .andExpect(jsonPath("$[0].name").value(testCustomerName))
                .andExpect(jsonPath("$[0].email").value(testCustomerEmail))
                .andExpect(jsonPath("$[0].image_url").value(testImageUrl));
    }

    @Test
    @DisplayName("should return empty list when no customers exist")
    void getAllCustomers_ReturnsEmptyListWhenNoCustomers() throws Exception {
        when(customersService.getAllCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("should return multiple customers")
    void getAllCustomers_ReturnsMultipleCustomers() throws Exception {
        Customer testCustomer1 = createTestCustomer();
        CustomerRead testCustomerRead1 = createTestCustomerRead();

        ObjectId secondCustomerId = new ObjectId();
        String secondName = faker.name().fullName();
        String secondEmail = faker.internet().emailAddress();

        Customer testCustomer2 = createTestCustomer();
        testCustomer2.set_id(secondCustomerId);
        testCustomer2.setName(secondName);
        testCustomer2.setEmail(secondEmail);

        CustomerRead testCustomerRead2 = new CustomerRead();
        testCustomerRead2.setId(secondCustomerId.toHexString());
        testCustomerRead2.setName(secondName);
        testCustomerRead2.setEmail(secondEmail);

        when(customersService.getAllCustomers()).thenReturn(List.of(testCustomer1, testCustomer2));
        when(customerMapper.toRead(testCustomer1)).thenReturn(testCustomerRead1);
        when(customerMapper.toRead(testCustomer2)).thenReturn(testCustomerRead2);

        mockMvc.perform(get("/customers/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
