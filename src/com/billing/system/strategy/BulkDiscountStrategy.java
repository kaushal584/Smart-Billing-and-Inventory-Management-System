package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import com.billing.system.model.InvoiceItem;
import java.math.BigDecimal;

public class BulkDiscountStrategy implements DiscountStrategy {
    private final int minQuantity;
    private final BigDecimal discountPercentage;

    public BulkDiscountStrategy(int minQuantity, double discountPercentage) {
        this.minQuantity = minQuantity;
        this.discountPercentage = BigDecimal.valueOf(discountPercentage);
    }

    @Override
    public BigDecimal calculateDiscount(Invoice invoice) {
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (InvoiceItem item : invoice.getItems()) {
            if (item.getQuantity() >= minQuantity) {
                BigDecimal itemTotal = item.getTotalPrice();
                BigDecimal discount = itemTotal.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
                totalDiscount = totalDiscount.add(discount);
            }
        }
        return totalDiscount;
    }
}
