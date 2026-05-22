package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import java.math.BigDecimal;

public interface TaxStrategy {
    BigDecimal calculateTax(Invoice invoice);
}
