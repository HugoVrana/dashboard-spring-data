package com.dashboard.service;

import com.dashboard.model.Invoice;
import com.dashboard.repository.IInvoiceRepository;
import com.dashboard.service.interfaces.IInvoiceService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Scope("singleton")
public class InvoiceService implements IInvoiceService {
    private final IInvoiceRepository invoiceRepository;

    public InvoiceService(IInvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public  List<Invoice> getInvoicesByStatus(String status){
        return invoiceRepository.findByStatus(status);
    }

    public List<Invoice> getLatestInvoice(Integer indexFrom, Integer indexTo) {
        return invoiceRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Invoice::getDate).reversed()) // latest first
                .skip(indexFrom)
                .limit(indexTo - indexFrom + 1)
                .toList();
    }

    public List<Invoice> searchInvoices(String searchTerm) {
        List<Invoice> unfilteredInvoices = invoiceRepository.findAll();
        List<Invoice> filteredInvoices = new ArrayList<>();
        String lowerCaseSearchTerm = searchTerm.toLowerCase();

        boolean dateParseed = false;
        LocalDate searchTermDate = null;
        try {
            searchTermDate = LocalDate.parse(lowerCaseSearchTerm, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dateParseed = true;
        }
        catch (Exception e) {
            // not necessary
        }

        for(Invoice invoice : unfilteredInvoices) {
            if (invoice.get_id().toString().toLowerCase().contains(lowerCaseSearchTerm))
            {
                filteredInvoices.add(invoice);
                continue;
            }
            if(invoice.getAmount().toString().contains(lowerCaseSearchTerm))
            {
                filteredInvoices.add(invoice);
                continue;
            }
            if(invoice.getDate().toString().contains(lowerCaseSearchTerm)){
                filteredInvoices.add(invoice);
                continue;
            }
            if(invoice.getStatus().toLowerCase().contains(lowerCaseSearchTerm))
            {
                filteredInvoices.add(invoice);
                continue;
            }
            if (invoice.getCustomer().get_id().toString().toLowerCase().contains(lowerCaseSearchTerm)){
                filteredInvoices.add(invoice);
                continue;
            }
            if (invoice.getCustomer().getName().toLowerCase().contains(lowerCaseSearchTerm))
            {
                filteredInvoices.add(invoice);
                continue;
            }
            if (invoice.getCustomer().getEmail().toLowerCase().contains(lowerCaseSearchTerm))
            {
                filteredInvoices.add(invoice);
                continue;
            }
           if (dateParseed & invoice.getDate().equals(searchTermDate)){
               filteredInvoices.add(invoice);
           }
        }
        return filteredInvoices;
    }
}