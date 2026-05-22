package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import java.math.BigDecimal;

public class IndiaGSTStrategy implements TaxStrategy {
    private final String companyState;

    public IndiaGSTStrategy(String companyState) {
        this.companyState = companyState;
    }

    @Override
    public BigDecimal calculateTax(Invoice invoice) {
        // String customerState = invoice.getCustomer().getState(); // Removed as state
        // is no longer in Customer model
        BigDecimal totalTax = BigDecimal.ZERO;

        // Determine if Intra-state (CGST + SGST) or Inter-state (IGST)
        // boolean isInterState = !companyState.equalsIgnoreCase(customerState);
        // Logic for splitting tax can be added here if reporting requires it.

        for (var item : invoice.getItems()) {
            BigDecimal price = item.getUnitPrice();
            String category = item.getProduct().getCategory();
            BigDecimal rate = getGSTRate(category, price);

            BigDecimal itemTotal = item.getTotalPrice();
            BigDecimal itemTax = itemTotal.multiply(rate).divide(BigDecimal.valueOf(100));

            totalTax = totalTax.add(itemTax);
        }

        return totalTax;
    }

    private BigDecimal getGSTRate(String category, BigDecimal price) {
        if (category == null)
            return BigDecimal.valueOf(12); // Default

        switch (category.toLowerCase()) {
            case "books":
            case "essential":
                return BigDecimal.ZERO;
            case "food":
                return BigDecimal.valueOf(5);
            case "electronics":
            case "services":
                return BigDecimal.valueOf(18);
            case "luxury":
                return BigDecimal.valueOf(28);
            default:
                return BigDecimal.valueOf(12);
        }
    }
}
