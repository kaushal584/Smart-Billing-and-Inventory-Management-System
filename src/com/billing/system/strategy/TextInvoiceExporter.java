package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import com.billing.system.model.InvoiceItem;
import java.math.BigDecimal;

public class TextInvoiceExporter implements InvoiceExporter {
    @Override
    public String export(Invoice invoice) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("             INVOICE\n");
        sb.append("========================================\n");
        sb.append("Invoice ID: ").append(invoice.getId()).append("\n");
        sb.append("Date:       ").append(invoice.getDate()).append("\n");
        sb.append("Customer:   ").append(invoice.getCustomer().getName()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s %5s %10s\n", "Item", "Qty", "Price"));
        sb.append("----------------------------------------\n");

        for (InvoiceItem item : invoice.getItems()) {
            sb.append(String.format("%-20s %5d %10.2f\n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getTotalPrice()));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal:   %10.2f\n", invoice.getTotalAmount())); // This is raw sum
        sb.append(String.format("Discount:   %10.2f\n", invoice.getDiscountAmount()));
        sb.append(String.format("Tax:        %10.2f\n", invoice.getTaxAmount()));
        sb.append("----------------------------------------\n");
        BigDecimal finalTotal = invoice.getTotalAmount()
                .subtract(invoice.getDiscountAmount())
                .add(invoice.getTaxAmount());
        sb.append(String.format("TOTAL:      %10.2f\n", finalTotal));
        sb.append("========================================\n");

        return sb.toString();
    }
}
