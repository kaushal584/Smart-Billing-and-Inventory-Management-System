package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import java.math.BigDecimal;

public class FixedAmountDiscountStrategy implements DiscountStrategy {
    private final BigDecimal amount;

    public FixedAmountDiscountStrategy(double amount) {
        this.amount = BigDecimal.valueOf(amount);
    }

    @Override
    public BigDecimal calculateDiscount(Invoice invoice) {
        return amount;
    }
}
