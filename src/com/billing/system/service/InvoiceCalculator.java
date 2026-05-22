package com.billing.system.service;

import com.billing.system.model.Invoice;
import com.billing.system.model.InvoiceItem;
import com.billing.system.strategy.DiscountStrategy;
import com.billing.system.strategy.TaxStrategy;
import java.math.BigDecimal;

public class InvoiceCalculator {

    public void calculate(Invoice invoice, DiscountStrategy discountStrategy, TaxStrategy taxStrategy) {
        // 1. Calculate Total Amount (Sum of items)
        BigDecimal totalAmount = invoice.getItems().stream()
                .map(InvoiceItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalAmount(totalAmount);

        // 2. Calculate Discount
        BigDecimal discount = discountStrategy.calculateDiscount(invoice);
        invoice.setDiscountAmount(discount);

        // 3. Calculate Tax
        BigDecimal tax = taxStrategy.calculateTax(invoice);
        invoice.setTaxAmount(tax);
    }
}
