package com.dashboard.controller.customers;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Story("Get Customer Count")
@DisplayName("GET /customers/count")
public class GetCustomerCountTest extends BaseCustomersControllerTest {
    @Test
    @DisplayName("should return customer count")
    void getCustomerCount_ReturnsCount() throws Exception {
        when(customersService.getCount()).thenReturn(42L);

        mockMvc.perform(get("/customers/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("42"));
    }

    @Test
    @DisplayName("should return zero when no customers exist")
    void getCustomerCount_ReturnsZeroWhenNoCustomers() throws Exception {
        when(customersService.getCount()).thenReturn(0L);

        mockMvc.perform(get("/customers/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("0"));
    }
}
