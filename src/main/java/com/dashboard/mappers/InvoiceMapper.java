package com.dashboard.mappers;

import com.dashboard.dataTransferObjects.InvoiceDto;
import com.dashboard.model.Invoice;
import org.springframework.stereotype.Service;

@Service
public class InvoiceMapper implements IInvoiceMapper {

    @Override
    public InvoiceDto toDto(Invoice invoice) {
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setId(invoice._id.toHexString());
        invoiceDto.setAmount(invoice.getAmount());
        invoiceDto.setStatus(invoice.getStatus());
        invoiceDto.setDate(invoice.getDate());
        return invoiceDto;
    }
}
