package com.billing.system.strategy;

import com.billing.system.model.Product;

public class StrategyFactory {

    public static DiscountStrategy getDiscountStrategy(String type, Double value, Product product) {
        if (type == null)
            return new FixedAmountDiscountStrategy(0);

        switch (type) {
            case "10% Off":
                return new PercentageDiscountStrategy(10);
            case "Rs 50 Off":
                return new FixedAmountDiscountStrategy(50);
            case "Buy 2 Get 1 Free":
            case "Buy 2 Get 1": // Handle potential variations
                if (product != null) {
                    return new BuyXGetYDiscountStrategy(product.getId(), 2, 1);
                }
                return new FixedAmountDiscountStrategy(0);
            case "Custom Amount":
                return new FixedAmountDiscountStrategy(value != null ? value : 0);
            case "Custom %":
                return new PercentageDiscountStrategy(value != null ? value : 0);
            default:
                if (type.startsWith("Buy 2 Get 1")) { // Fallback for partial match
                    if (product != null) {
                        return new BuyXGetYDiscountStrategy(product.getId(), 2, 1);
                    }
                }
                return new FixedAmountDiscountStrategy(0);
        }
    }

    public static TaxStrategy getTaxStrategy(String type, Double value) {
        if (type == null)
            return new SimpleTaxStrategy(0);

        switch (type) {
            case "Simple 10%":
                return new SimpleTaxStrategy(10);
            case "India GST (Delhi)":
                return new IndiaGSTStrategy("Delhi");
            case "India GST (Mumbai)":
                return new IndiaGSTStrategy("Mumbai");
            case "Custom Tax %":
                return new SimpleTaxStrategy(value != null ? value : 0);
            default:
                return new SimpleTaxStrategy(0);
        }
    }
}
