package com.dashboard.controller.customers;

import com.dashboard.common.logging.GrafanaHttpClient;
import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.model.entities.Customer;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomersControllerTest extends BaseCustomersControllerTest {

    @MockitoBean
    private GrafanaHttpClient grafanaHttpClient;

    private CustomerRead createTestCustomerRead() {
        CustomerRead customerRead = new CustomerRead();
        customerRead.setId(testCustomerId.toHexString());
        customerRead.setName(testCustomerName);
        customerRead.setEmail(testCustomerEmail);
        customerRead.setImage_url(testImageUrl);
        return customerRead;
    }

    @Nested
    @DisplayName("GET /customers/")
    class GetAllCustomersTests {

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

    @Nested
    @DisplayName("GET /customers/{id}")
    class GetCustomerByIdTests {

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

    @Nested
    @DisplayName("GET /customers/count")
    class GetCustomerCountTests {

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
}
