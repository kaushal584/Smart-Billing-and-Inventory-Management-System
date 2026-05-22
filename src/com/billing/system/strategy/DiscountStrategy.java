package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculateDiscount(Invoice invoice);
}
