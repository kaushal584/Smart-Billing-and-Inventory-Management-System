package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import com.billing.system.model.InvoiceItem;
import java.math.BigDecimal;

public class BuyXGetYDiscountStrategy implements DiscountStrategy {
    private final String targetProductId;
    private final int buyCount;
    private final int freeCount;

    public BuyXGetYDiscountStrategy(String targetProductId, int buyCount, int freeCount) {
        this.targetProductId = targetProductId;
        this.buyCount = buyCount;
        this.freeCount = freeCount;
    }

    @Override
    public BigDecimal calculateDiscount(Invoice invoice) {
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (InvoiceItem item : invoice.getItems()) {
            if (item.getProduct().getId().equals(targetProductId)) {
                int quantity = item.getQuantity();
                int setSize = buyCount + freeCount;
                int freeItems = (quantity / setSize) * freeCount;

                // Add remaining items logic if needed, usually it's just full sets
                // Example: Buy 2 Get 1 Free. If you buy 3, you pay for 2. Discount = 1 * price.

                if (freeItems > 0) {
                    totalDiscount = totalDiscount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(freeItems)));
                }
            }
        }
        return totalDiscount;
    }
}
