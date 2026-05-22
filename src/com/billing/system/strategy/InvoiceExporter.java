package com.billing.system.strategy;

import com.billing.system.model.Invoice;

public interface InvoiceExporter {
    String export(Invoice invoice);
}
