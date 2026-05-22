package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import java.math.BigDecimal;

public class PercentageDiscountStrategy implements DiscountStrategy {
    private final BigDecimal percentage;

    public PercentageDiscountStrategy(double percentage) {
        this.percentage = BigDecimal.valueOf(percentage);
    }

    @Override
    public BigDecimal calculateDiscount(Invoice invoice) {
        BigDecimal subtotal = invoice.getItems().stream()
                .map(item -> item.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return subtotal.multiply(percentage).divide(BigDecimal.valueOf(100));
    }
}
