package com.billing.system.repository;

import com.billing.system.model.Invoice;
import com.billing.system.model.Customer;
import com.billing.system.model.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CSVInvoiceRepository extends CSVRepository<Invoice> {
    // We need access to other repos to reconstruct objects, but for CSV simplicity
    // we might just store IDs or minimal data.
    // Ideally, we should inject Product/Customer repos here to fully reconstruct.
    // For this demo, we will store a simplified JSON-like or pipe-separated
    // structure for items.

    // However, to keep it SOLID and simple without complex DI loops:
    // We will just store the raw data and reconstruct what we can.
    // A proper DB is better, but for CSV:
    // ID,CustomerID,Date,Status,Total,Tax,Discount,Item1|Item2|...

    // To properly reconstruct, we'll need to fetch Customer and Product by ID.
    // Let's assume we can pass them in or just store copies of data.
    // For this exercise, I'll implement a "Standalone" approach where we store
    // enough info to display history.

    // Actually, the user wants "realtime updates".
    // Let's try to do it properly by passing repos if possible, or just storing
    // flat data.
    // I'll stick to flat data for simplicity of the file format.

    public CSVInvoiceRepository() {
        super("invoices.csv");
    }

    @Override
    protected Invoice parseLine(String line) {
        // This is tricky without a full parser.
        // Let's assume a simplified format where we don't fully reconstruct the object
        // graph
        // from other files, but just load what's in the invoice line.
        // OR, we just don't implement full load for Invoices in this simple CSV demo
        // if the user just wants to "save" them.
        // But the user said "shown in gui".

        // Let's do a best-effort parsing.
        // Format: ID,CustomerName,Date,Status,Total,Tax,Discount
        // We won't reconstruct items perfectly from CSV in this simple version unless
        // we use a better format (JSON/XML).
        // But I will try to support basic fields.

        String[] parts = line.split(",");
        if (parts.length < 7)
            return null;

        // We create a "dummy" customer object just to hold the name for display
        Customer c = new Customer("unknown", parts[1], "", "", Customer.CustomerType.REGULAR);

        Invoice invoice = new Invoice(c);
        invoice.setId(parts[0]);

        invoice.setDate(LocalDateTime.parse(parts[2]));
        invoice.setStatus(InvoiceStatus.valueOf(parts[3]));
        invoice.setTotalAmount(new BigDecimal(parts[4]));
        invoice.setTaxAmount(new BigDecimal(parts[5]));
        invoice.setDiscountAmount(new BigDecimal(parts[6]));

        return invoice;
    }

    @Override
    protected String toCSV(Invoice i) {
        return String.join(",",
                i.getId(),
                i.getCustomer().getName(),
                i.getDate().toString(),
                i.getStatus().name(),
                i.getTotalAmount().toString(),
                i.getTaxAmount().toString(),
                i.getDiscountAmount().toString());
    }

    @Override
    protected String getId(Invoice i) {
        return i.getId();
    }
}
