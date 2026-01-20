package com.dashboard.service;

import com.dashboard.common.model.Audit;
import com.dashboard.model.entities.Customer;
import com.dashboard.model.entities.Invoice;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.service.interfaces.IInvoiceSearchService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private IInvoiceRepository invoiceRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private IInvoiceSearchService invoiceSearchService;

    @InjectMocks
    private InvoiceService invoiceService;

    private Invoice testInvoice;
    private ObjectId testInvoiceId;

    @BeforeEach
    void setUp() {
        ObjectId testCustomerId = new ObjectId();
        Customer testCustomer = new Customer();
        testCustomer.set_id(testCustomerId);
        testCustomer.setName("Acme Corp");
        testCustomer.setEmail("billing@acme.com");
        testCustomer.setAudit(new Audit());

        testInvoiceId = new ObjectId();
        testInvoice = new Invoice();
        testInvoice.set_id(testInvoiceId);
        testInvoice.setCustomer(testCustomer);
        testInvoice.setAmount(1500.00);
        testInvoice.setDate(LocalDate.now());
        testInvoice.setStatus("pending");
        testInvoice.setAudit(new Audit());
    }

    @Nested
    @DisplayName("getAllInvoices")
    class GetAllInvoicesTests {

        @Test
        @DisplayName("should return all non-deleted invoices")
        void getAllInvoices_ReturnsAllNonDeletedInvoices() {
            List<Invoice> expectedInvoices = List.of(testInvoice);
            when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(expectedInvoices);

            List<Invoice> result = invoiceService.getAllInvoices();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(testInvoice);
            verify(invoiceRepository).findByAudit_DeletedAtIsNull();
        }

        @Test
        @DisplayName("should return empty list when no invoices exist")
        void getAllInvoices_ReturnsEmptyListWhenNoInvoices() {
            when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(Collections.emptyList());

            List<Invoice> result = invoiceService.getAllInvoices();

            assertThat(result).isEmpty();
            verify(invoiceRepository).findByAudit_DeletedAtIsNull();
        }
    }

    @Nested
    @DisplayName("getInvoicesByStatus")
    class GetInvoicesByStatusTests {

        @Test
        @DisplayName("should return invoices with pending status")
        void getInvoicesByStatus_ReturnsInvoicesWithPendingStatus() {
            String status = "pending";
            List<Invoice> expectedInvoices = List.of(testInvoice);
            when(invoiceRepository.findByStatusAndAudit_DeletedAtIsNull(status))
                    .thenReturn(expectedInvoices);

            List<Invoice> result = invoiceService.getInvoicesByStatus(status);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getStatus()).isEqualTo(status);
            verify(invoiceRepository).findByStatusAndAudit_DeletedAtIsNull(status);
        }

        @Test
        @DisplayName("should return invoices with paid status")
        void getInvoicesByStatus_ReturnsInvoicesWithPaidStatus() {
            String status = "paid";
            Invoice paidInvoice = new Invoice();
            paidInvoice.set_id(new ObjectId());
            paidInvoice.setStatus("paid");
            paidInvoice.setAudit(new Audit());
            List<Invoice> expectedInvoices = List.of(paidInvoice);
            when(invoiceRepository.findByStatusAndAudit_DeletedAtIsNull(status))
                    .thenReturn(expectedInvoices);

            List<Invoice> result = invoiceService.getInvoicesByStatus(status);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getStatus()).isEqualTo("paid");
            verify(invoiceRepository).findByStatusAndAudit_DeletedAtIsNull(status);
        }

        @Test
        @DisplayName("should return empty list when no invoices match status")
        void getInvoicesByStatus_ReturnsEmptyListWhenNoMatch() {
            String status = "cancelled";
            when(invoiceRepository.findByStatusAndAudit_DeletedAtIsNull(status))
                    .thenReturn(Collections.emptyList());

            List<Invoice> result = invoiceService.getInvoicesByStatus(status);

            assertThat(result).isEmpty();
            verify(invoiceRepository).findByStatusAndAudit_DeletedAtIsNull(status);
        }
    }

    @Nested
    @DisplayName("getLatestInvoice")
    class GetLatestInvoiceTests {

        @Test
        @DisplayName("should return latest invoices within range")
        void getLatestInvoice_ReturnsLatestInvoicesWithinRange() {
            Invoice invoice1 = new Invoice();
            invoice1.set_id(new ObjectId());
            invoice1.setDate(LocalDate.now().minusDays(1));
            invoice1.setAudit(new Audit());

            Invoice invoice2 = new Invoice();
            invoice2.set_id(new ObjectId());
            invoice2.setDate(LocalDate.now());
            invoice2.setAudit(new Audit());

            List<Invoice> allInvoices = List.of(invoice1, invoice2);
            when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(allInvoices);

            List<Invoice> result = invoiceService.getLatestInvoice(0, 1);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getDate()).isAfterOrEqualTo(result.get(1).getDate());
            verify(invoiceRepository).findByAudit_DeletedAtIsNull();
        }

        @Test
        @DisplayName("should return empty list when no invoices exist")
        void getLatestInvoice_ReturnsEmptyListWhenNoInvoices() {
            when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(Collections.emptyList());

            List<Invoice> result = invoiceService.getLatestInvoice(0, 5);

            assertThat(result).isEmpty();
            verify(invoiceRepository).findByAudit_DeletedAtIsNull();
        }

        @Test
        @DisplayName("should skip invoices based on indexFrom")
        void getLatestInvoice_SkipsInvoicesBasedOnIndexFrom() {
            Invoice invoice1 = new Invoice();
            invoice1.set_id(new ObjectId());
            invoice1.setDate(LocalDate.now().minusDays(2));
            invoice1.setAudit(new Audit());

            Invoice invoice2 = new Invoice();
            invoice2.set_id(new ObjectId());
            invoice2.setDate(LocalDate.now().minusDays(1));
            invoice2.setAudit(new Audit());

            Invoice invoice3 = new Invoice();
            invoice3.set_id(new ObjectId());
            invoice3.setDate(LocalDate.now());
            invoice3.setAudit(new Audit());

            List<Invoice> allInvoices = List.of(invoice1, invoice2, invoice3);
            when(invoiceRepository.findByAudit_DeletedAtIsNull()).thenReturn(allInvoices);

            List<Invoice> result = invoiceService.getLatestInvoice(1, 2);

            assertThat(result).hasSize(2);
            verify(invoiceRepository).findByAudit_DeletedAtIsNull();
        }
    }

    @Nested
    @DisplayName("getInvoiceById")
    class GetInvoiceByIdTests {

        @Test
        @DisplayName("should return invoice when found by id")
        void getInvoiceById_ReturnsInvoiceWhenFound() {
            when(invoiceRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId))
                    .thenReturn(Optional.of(testInvoice));

            Optional<Invoice> result = invoiceService.getInvoiceById(testInvoiceId);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testInvoice);
            verify(invoiceRepository).findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId);
        }

        @Test
        @DisplayName("should return empty when invoice not found")
        void getInvoiceById_ReturnsEmptyWhenNotFound() {
            when(invoiceRepository.findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId))
                    .thenReturn(Optional.empty());

            Optional<Invoice> result = invoiceService.getInvoiceById(testInvoiceId);

            assertThat(result).isEmpty();
            verify(invoiceRepository).findBy_idEqualsAndAudit_DeletedAtIsNull(testInvoiceId);
        }
    }

    @Nested
    @DisplayName("insertInvoice")
    class InsertInvoiceTests {

        @Test
        @DisplayName("should insert invoice and sync with search service")
        void insertInvoice_InsertsAndSyncsInvoice() {
            when(invoiceRepository.insert(testInvoice)).thenReturn(testInvoice);

            Invoice result = invoiceService.insertInvoice(testInvoice);

            assertThat(result).isEqualTo(testInvoice);
            verify(invoiceRepository).insert(testInvoice);
            verify(invoiceSearchService).syncInvoice(testInvoice);
        }
    }

    @Nested
    @DisplayName("updateInvoice")
    class UpdateInvoiceTests {

        @Test
        @DisplayName("should update invoice and sync with search service")
        void updateInvoice_UpdatesAndSyncsInvoice() {
            testInvoice.setStatus("paid");
            when(invoiceRepository.save(testInvoice)).thenReturn(testInvoice);

            Invoice result = invoiceService.updateInvoice(testInvoice);

            assertThat(result).isEqualTo(testInvoice);
            assertThat(result.getStatus()).isEqualTo("paid");
            verify(invoiceRepository).save(testInvoice);
            verify(invoiceSearchService).syncInvoice(testInvoice);
        }
    }

    @Nested
    @DisplayName("searchInvoices")
    class SearchInvoicesTests {

        @Test
        @DisplayName("should return all invoices when search term is null")
        void searchInvoices_ReturnsAllInvoicesWhenTermIsNull() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Invoice> invoices = List.of(testInvoice);
            when(mongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(invoices);
            when(mongoTemplate.count(any(Query.class), eq(Invoice.class))).thenReturn(1L);

            Page<Invoice> result = invoiceService.searchInvoices(null, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(mongoTemplate).find(any(Query.class), eq(Invoice.class));
        }

        @Test
        @DisplayName("should return all invoices when search term is empty")
        void searchInvoices_ReturnsAllInvoicesWhenTermIsEmpty() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Invoice> invoices = List.of(testInvoice);
            when(mongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(invoices);
            when(mongoTemplate.count(any(Query.class), eq(Invoice.class))).thenReturn(1L);

            Page<Invoice> result = invoiceService.searchInvoices("", pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(mongoTemplate).find(any(Query.class), eq(Invoice.class));
        }

        @Test
        @DisplayName("should return all invoices when search term is whitespace")
        void searchInvoices_ReturnsAllInvoicesWhenTermIsWhitespace() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Invoice> invoices = List.of(testInvoice);
            when(mongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(invoices);
            when(mongoTemplate.count(any(Query.class), eq(Invoice.class))).thenReturn(1L);

            Page<Invoice> result = invoiceService.searchInvoices("   ", pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(mongoTemplate).find(any(Query.class), eq(Invoice.class));
        }

        @Test
        @DisplayName("should search by ObjectId when term is valid ObjectId")
        void searchInvoices_SearchesByObjectIdWhenTermIsValidObjectId() {
            Pageable pageable = PageRequest.of(0, 10);
            String objectIdTerm = testInvoiceId.toHexString();
            List<Invoice> invoices = List.of(testInvoice);
            when(mongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(invoices);

            Page<Invoice> result = invoiceService.searchInvoices(objectIdTerm, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(mongoTemplate).find(any(Query.class), eq(Invoice.class));
        }
    }
}
