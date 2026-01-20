package com.dashboard.controller.invoices;

import com.dashboard.dataTransferObject.customer.CustomerRead;
import com.dashboard.dataTransferObject.invoice.InvoiceCreate;
import com.dashboard.dataTransferObject.invoice.InvoiceRead;
import com.dashboard.dataTransferObject.invoice.InvoiceUpdate;
import com.dashboard.dataTransferObject.page.PageRequest;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.model.entities.InvoiceSearchDocument;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InvoicesControllerTest extends BaseInvoicesControllerTest {

    private InvoiceRead createTestInvoiceRead(Invoice invoice) {
        InvoiceRead invoiceRead = new InvoiceRead();
        invoiceRead.setId(invoice.get_id().toHexString());
        invoiceRead.setAmount(invoice.getAmount());
        invoiceRead.setDate(invoice.getDate());
        invoiceRead.setStatus(invoice.getStatus());

        CustomerRead customerRead = new CustomerRead();
        customerRead.setId(invoice.getCustomer().get_id().toHexString());
        customerRead.setName(invoice.getCustomer().getName());
        customerRead.setEmail(invoice.getCustomer().getEmail());
        customerRead.setImage_url(invoice.getCustomer().getImage_url());
        invoiceRead.setCustomer(customerRead);

        return invoiceRead;
    }

    private InvoiceSearchDocument createTestInvoiceSearchDocument(Invoice invoice) {
        InvoiceSearchDocument doc = new InvoiceSearchDocument();
        doc.set_id(new ObjectId());
        doc.setInvoiceId(invoice.get_id());
        doc.setCustomerId(invoice.getCustomer().get_id());
        doc.setAmount(invoice.getAmount());
        doc.setDate(invoice.getDate());
        doc.setStatus(invoice.getStatus());
        doc.setCustomerName(invoice.getCustomer().getName());
        doc.setCustomerEmail(invoice.getCustomer().getEmail());
        doc.setCustomerImageUrl(invoice.getCustomer().getImage_url());
        return doc;
    }

    @Nested
    @DisplayName("GET /invoices/")
    class GetAllInvoicesTests {

        @Test
        @DisplayName("should return all invoices")
        void getAllInvoices_ReturnsAllInvoices() throws Exception {
            Invoice testInvoice = createTestInvoice();
            InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
            CustomerRead customerRead = testInvoiceRead.getCustomer();

            when(invoiceService.getAllInvoices()).thenReturn(List.of(testInvoice));
            when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
            when(customerMapper.toRead(testInvoice.getCustomer())).thenReturn(customerRead);

            mockMvc.perform(get("/invoices/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(testInvoiceId.toHexString()))
                    .andExpect(jsonPath("$[0].amount").value(testAmount))
                    .andExpect(jsonPath("$[0].status").value(testStatus));
        }

        @Test
        @DisplayName("should return empty list when no invoices exist")
        void getAllInvoices_ReturnsEmptyListWhenNoInvoices() throws Exception {
            when(invoiceService.getAllInvoices()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/invoices/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /invoices/{id}")
    class GetInvoiceByIdTests {

        @Test
        @DisplayName("should return invoice when found")
        void getInvoiceById_ReturnsInvoiceWhenFound() throws Exception {
            Invoice testInvoice = createTestInvoice();
            InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
            CustomerRead customerRead = testInvoiceRead.getCustomer();

            when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.of(testInvoice));
            when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
            when(customerMapper.toRead(testInvoice.getCustomer())).thenReturn(customerRead);

            mockMvc.perform(get("/invoices/{id}", testInvoiceId.toHexString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testInvoiceId.toHexString()))
                    .andExpect(jsonPath("$.amount").value(testAmount))
                    .andExpect(jsonPath("$.status").value(testStatus));
        }

        @Test
        @DisplayName("should return 404 when invoice not found")
        void getInvoiceById_Returns404WhenNotFound() throws Exception {
            when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.empty());

            mockMvc.perform(get("/invoices/{id}", testInvoiceId.toHexString()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 404 when id is invalid")
        void getInvoiceById_Returns404WhenIdInvalid() throws Exception {
            mockMvc.perform(get("/invoices/{id}", "invalid-id"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /invoices/latest")
    class GetLatestInvoicesTests {

        @Test
        @DisplayName("should return all invoices when no indices provided")
        void getLatestInvoices_ReturnsAllWhenNoIndices() throws Exception {
            Invoice testInvoice = createTestInvoice();
            InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
            CustomerRead customerRead = testInvoiceRead.getCustomer();

            when(invoiceService.getAllInvoices()).thenReturn(List.of(testInvoice));
            when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
            when(customerMapper.toRead(testInvoice.getCustomer())).thenReturn(customerRead);

            mockMvc.perform(get("/invoices/latest"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("should return invoices in range when indices provided")
        void getLatestInvoices_ReturnsRangeWhenIndicesProvided() throws Exception {
            Invoice testInvoice = createTestInvoice();
            InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
            CustomerRead customerRead = testInvoiceRead.getCustomer();

            when(invoiceService.getLatestInvoice(0, 5)).thenReturn(List.of(testInvoice));
            when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
            when(customerMapper.toRead(testInvoice.getCustomer())).thenReturn(customerRead);

            mockMvc.perform(get("/invoices/latest")
                            .param("indexFrom", "0")
                            .param("indexTo", "5"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("should return 500 when indexFrom > indexTo")
        void getLatestInvoices_ThrowsWhenIndexFromGreaterThanIndexTo() throws Exception {
            mockMvc.perform(get("/invoices/latest")
                            .param("indexFrom", "10")
                            .param("indexTo", "5"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /invoices/count")
    class GetInvoiceCountTests {

        @Test
        @DisplayName("should return total count when no status provided")
        void getInvoiceCount_ReturnsTotalCount() throws Exception {
            Invoice invoice1 = createTestInvoice();
            Invoice invoice2 = createTestInvoice();
            invoice2.set_id(new ObjectId());

            when(invoiceService.getAllInvoices()).thenReturn(List.of(invoice1, invoice2));

            mockMvc.perform(get("/invoices/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("2"));
        }

        @Test
        @DisplayName("should return count by status when status provided")
        void getInvoiceCount_ReturnsCountByStatus() throws Exception {
            Invoice testInvoice = createTestInvoice();

            when(invoiceService.getInvoicesByStatus("pending")).thenReturn(List.of(testInvoice));

            mockMvc.perform(get("/invoices/count")
                            .param("status", "pending"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("1"));
        }
    }

    @Nested
    @DisplayName("GET /invoices/amount")
    class GetInvoiceAmountTests {

        @Test
        @DisplayName("should return total amount when no status provided")
        void getInvoiceAmount_ReturnsTotalAmount() throws Exception {
            Invoice invoice1 = createTestInvoice();
            invoice1.setAmount(100.0);
            Invoice invoice2 = createTestInvoice();
            invoice2.set_id(new ObjectId());
            invoice2.setAmount(200.0);

            when(invoiceService.getAllInvoices()).thenReturn(List.of(invoice1, invoice2));

            mockMvc.perform(get("/invoices/amount"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("300.0"));
        }

        @Test
        @DisplayName("should return amount by status when status provided")
        void getInvoiceAmount_ReturnsAmountByStatus() throws Exception {
            Invoice testInvoice = createTestInvoice();
            testInvoice.setAmount(150.0);

            when(invoiceService.getInvoicesByStatus("paid")).thenReturn(List.of(testInvoice));

            mockMvc.perform(get("/invoices/amount")
                            .param("status", "paid"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("150.0"));
        }
    }

    @Nested
    @DisplayName("GET /invoices/pages")
    class GetPagesTests {

        @Test
        @DisplayName("should return total pages")
        void getPages_ReturnsTotalPages() throws Exception {
            Page<InvoiceSearchDocument> page = new PageImpl<>(Collections.emptyList(), Pageable.ofSize(15), 45);

            when(invoiceSearchService.search(any(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/invoices/pages"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("3"));
        }

        @Test
        @DisplayName("should use custom size when provided")
        void getPages_UsesCustomSize() throws Exception {
            Page<InvoiceSearchDocument> page = new PageImpl<>(Collections.emptyList(), Pageable.ofSize(10), 25);

            when(invoiceSearchService.search(any(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/invoices/pages")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("3"));
        }
    }

    @Nested
    @DisplayName("POST /invoices/search")
    class SearchInvoicesTests {

        @Test
        @DisplayName("should return paginated results when search succeeds")
        void searchInvoices_ReturnsPaginatedResults() throws Exception {
            Invoice testInvoice = createTestInvoice();
            InvoiceSearchDocument searchDoc = createTestInvoiceSearchDocument(testInvoice);
            InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);

            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(1);
            pageRequest.setSize(10);
            pageRequest.setSearch("test");

            Page<InvoiceSearchDocument> searchPage = new PageImpl<>(List.of(searchDoc), Pageable.ofSize(10), 1);

            when(invoiceSearchService.search(eq("test"), any(Pageable.class))).thenReturn(searchPage);
            when(invoiceSearchMapper.toRead(searchDoc)).thenReturn(testInvoiceRead);

            mockMvc.perform(post("/invoices/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pageRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.currentPage").value(1));
        }

        @Test
        @DisplayName("should return 204 when no results found")
        void searchInvoices_Returns204WhenNoResults() throws Exception {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(1);
            pageRequest.setSize(10);
            pageRequest.setSearch("nonexistent");

            Page<InvoiceSearchDocument> emptyPage = Page.empty();
            when(invoiceSearchService.search(eq("nonexistent"), any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(post("/invoices/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pageRequest)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 500 when page is zero or negative")
        void searchInvoices_ThrowsWhenPageIsInvalid() throws Exception {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(0);
            pageRequest.setSize(10);
            pageRequest.setSearch("test");

            mockMvc.perform(post("/invoices/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pageRequest)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /invoices")
    class CreateInvoiceTests {

        @Test
        @DisplayName("should create invoice successfully")
        void createInvoice_CreatesInvoiceSuccessfully() throws Exception {
            Invoice testInvoice = createTestInvoice();
            Customer customer = testInvoice.getCustomer();
            InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
            CustomerRead customerRead = testInvoiceRead.getCustomer();

            InvoiceCreate invoiceCreate = new InvoiceCreate("pending", 1000.0, testCustomerId.toHexString());

            when(customersService.getCustomer(testCustomerId)).thenReturn(Optional.of(customer));
            when(invoiceMapper.toModel(any(InvoiceCreate.class), any(Customer.class))).thenReturn(testInvoice);
            when(invoiceService.insertInvoice(any(Invoice.class))).thenReturn(testInvoice);
            when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
            when(customerMapper.toRead(customer)).thenReturn(customerRead);

            mockMvc.perform(post("/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invoiceCreate)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.status").value(testStatus));
        }

        @Test
        @DisplayName("should return 500 when customer not found (NotFoundException not handled)")
        void createInvoice_Returns500WhenCustomerNotFound() throws Exception {
            InvoiceCreate invoiceCreate = new InvoiceCreate("pending", 1000.0, testCustomerId.toHexString());

            when(customersService.getCustomer(testCustomerId)).thenReturn(Optional.empty());

            mockMvc.perform(post("/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invoiceCreate)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("PUT /invoices/{id}")
    class UpdateInvoiceTests {

        @Test
        @DisplayName("should update invoice successfully")
        void updateInvoice_UpdatesInvoiceSuccessfully() throws Exception {
            Invoice testInvoice = createTestInvoice();
            Customer customer = testInvoice.getCustomer();
            InvoiceRead testInvoiceRead = createTestInvoiceRead(testInvoice);
            CustomerRead customerRead = testInvoiceRead.getCustomer();

            InvoiceUpdate invoiceUpdate = new InvoiceUpdate(testInvoiceId.toHexString(), "paid", 1500.0, testCustomerId.toHexString());

            when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.of(testInvoice));
            when(customersService.getCustomer(testCustomerId)).thenReturn(Optional.of(customer));
            when(invoiceMapper.toModel(any(InvoiceUpdate.class), any(Customer.class))).thenReturn(testInvoice);
            when(invoiceService.updateInvoice(any(Invoice.class))).thenReturn(testInvoice);
            when(invoiceMapper.toRead(testInvoice)).thenReturn(testInvoiceRead);
            when(customerMapper.toRead(customer)).thenReturn(customerRead);

            mockMvc.perform(put("/invoices/{id}", testInvoiceId.toHexString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invoiceUpdate)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"));
        }

        @Test
        @DisplayName("should return 404 when invoice not found")
        void updateInvoice_Returns404WhenInvoiceNotFound() throws Exception {
            InvoiceUpdate invoiceUpdate = new InvoiceUpdate(testInvoiceId.toHexString(), "paid", 1500.0, testCustomerId.toHexString());

            when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.empty());

            mockMvc.perform(put("/invoices/{id}", testInvoiceId.toHexString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invoiceUpdate)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 404 when id is invalid")
        void updateInvoice_Returns404WhenIdInvalid() throws Exception {
            InvoiceUpdate invoiceUpdate = new InvoiceUpdate("invalid-id", "paid", 1500.0, testCustomerId.toHexString());

            mockMvc.perform(put("/invoices/{id}", "invalid-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invoiceUpdate)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /invoices/{id}")
    class DeleteInvoiceTests {

        @Test
        @DisplayName("should delete invoice successfully")
        void deleteInvoice_DeletesInvoiceSuccessfully() throws Exception {
            Invoice testInvoice = createTestInvoice();

            when(invoiceService.getInvoiceById(testInvoiceId))
                    .thenReturn(Optional.of(testInvoice))
                    .thenReturn(Optional.empty());
            when(invoiceService.updateInvoice(any(Invoice.class))).thenReturn(testInvoice);

            mockMvc.perform(delete("/invoices/{id}", testInvoiceId.toHexString()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("1"));
        }

        @Test
        @DisplayName("should return 404 when invoice not found")
        void deleteInvoice_Returns404WhenNotFound() throws Exception {
            when(invoiceService.getInvoiceById(testInvoiceId)).thenReturn(Optional.empty());

            mockMvc.perform(delete("/invoices/{id}", testInvoiceId.toHexString()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 404 when id is invalid")
        void deleteInvoice_Returns404WhenIdInvalid() throws Exception {
            mockMvc.perform(delete("/invoices/{id}", "invalid-id"))
                    .andExpect(status().isNotFound());
        }
    }
}
