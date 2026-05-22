package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import java.math.BigDecimal;

public class SimpleTaxStrategy implements TaxStrategy {
    private final BigDecimal taxRate;

    public SimpleTaxStrategy(double taxRate) {
        this.taxRate = BigDecimal.valueOf(taxRate);
    }

    @Override
    public BigDecimal calculateTax(Invoice invoice) {
        // Tax is usually applied on the discounted amount
        BigDecimal taxableAmount = invoice.getTotalAmount().subtract(invoice.getDiscountAmount());
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }
        return taxableAmount.multiply(taxRate).divide(BigDecimal.valueOf(100));
    }
}
